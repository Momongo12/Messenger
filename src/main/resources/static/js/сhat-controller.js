const xhr = new XMLHttpRequest();
const chatList = document.getElementById('chat-list');
const sendMessageButton = document.getElementById("send-message-button");
const messageInput = document.getElementById('message-input')
const searchInput = document.getElementById("chats-search-input");
const emojiItems = document.querySelectorAll('.emoji-item');
const footerChat = document.querySelector(".footer-chat");
let emojiContainer = document.querySelector('.smiley-icon-container');
let emojiMenu = document.querySelector('.smiley-menu');
let secondInterlocutorUniqueUsername;
let chatId = null;


setInterval(() => {
    if (chatId != null && chatId !== '0') {
        xhr.open("GET", `/chats/${chatId}/messages`, true)
        xhr.onload = function () {
            const response = JSON.parse(xhr.responseText);
            displayChatMessages(response.messages);
        }
        xhr.send();
    }
}, 3000)

setInterval(() => {
    if (searchInput.value === '') {
        xhr.open("GET", "/api/chats", true);
        xhr.onload = function () {
            const response = JSON.parse(xhr.responseText);
            chatList.innerHTML = '';
            displayChatsAndUsersList(response.chatsList);
        }
        xhr.send();
    }
}, 5000)

function displayChatMessages(messages) {
    const chatMessages = document.getElementById('messages-chat');
    chatMessages.innerHTML = '';

    for (const message of messages) {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message');
        messageElement.innerHTML = `
            <div class="message">
              <div class="photo" style="background-image: url('${message.userAvatarUrl}')"></div>
              <div class="text-block">
                <div class="info-message">
                  <a href="/home/${message.senderId}" class="message-details username">${message.senderName}</a>
                  <p class="message-details">${message.departureTime}</p>
                </div>
                <div class="message-text">
                  <p>${message.text}</p>
                </div>
              </div>
            </div>
            `;
        if (message.subMessages != null) {
            const messageTextBlock = messageElement.querySelector(".message-text");
            for (const subMessage of message.subMessages) {
                const messageTextParagraph = document.createElement('p');
                messageTextParagraph.textContent = subMessage;
                messageTextBlock.appendChild(messageTextParagraph);
            }
        }
        chatMessages.appendChild(messageElement);
    }

    chatMessages.scrollTop = chatMessages.scrollHeight;
}

function displayChatsAndUsersList(chatsData) {
    chatsData.forEach(chat => {
        const chatDiv = document.createElement('div');
        chatDiv.classList.add('chat-list', 'discussion', 'message-active');
        chatDiv.setAttribute('data-chat-id', chat.chatId);
        chatDiv.innerHTML = `
                        <div class="photo" style="background-image: url('${chat.chatAvatarImageUrl}')">
                            <div class="${chat.interlocutorStatus}"></div>
                        </div>
                        <div class="desc-contact">
                            <p class="name">${(chat.chatId === 0) ? chat.username: chat.chatName}</p>
                            <p class="message unique-username">${(chat.chatId === 0) ? '@' + chat.uniqueUsername: chat.lastMessage}</p>
                        </div>
                    `;
        chatList.appendChild(chatDiv);
    });
}

chatList.addEventListener('click', event => {
    footerChat.style.display = 'flex';
    chatId = event.target.closest('[data-chat-id]').dataset.chatId;

    secondInterlocutorUniqueUsername = event.target.closest('[data-chat-id]').querySelector(".unique-username").innerText.trim();

    // Отправка AJAX-запроса на сервер для получения истории сообщений
    xhr.open('GET', `/chats/${chatId}/messages`);
    xhr.onload = () => {
        const response = JSON.parse(xhr.responseText);

        const chatTitle = document.getElementById('chat-title');
        chatTitle.textContent = event.target.closest('[data-chat-id]').querySelector(".name").innerText;

        displayChatMessages(response.messages);
    };
    xhr.send();
});

sendMessageButton.addEventListener('click', event => {
    const currentDate = new Date();
    let createChatFlag = true;
    if (chatId === '0'){
        const data = {
            lastMessage: messageInput.value,
            createdAt: currentDate.toISOString(),
            membersNumber: 2,
        }
        xhr.open('POST', "api/chats/" + secondInterlocutorUniqueUsername.substring(1), false);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onload = function () {
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                chatId = response.currentChatId;
                chatList.innerHTML = '';
                displayChatsAndUsersList(response.chatsList);
            } else {
                console.log("Creating chat error");
                createChatFlag = false;
            }
        };
        xhr.send(JSON.stringify(data));
    }

    if (!createChatFlag) return;

    const data = {
        chatId: chatId,
        text: messageInput.value,
        date: currentDate.toISOString()
    };

    messageInput.value = '';

    xhr.open('POST', "/chats/messages");
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function () {
        const response = JSON.parse(xhr.responseText);
        //добавь обработку кода 500 при добавке сообщения
        displayChatMessages(response.messages)
    };
    xhr.send(JSON.stringify(data));
});

searchInput.addEventListener('input', (event) => {
    const inputValue = event.target.value.trim();
    if (inputValue.startsWith('@')) {
        const searchValue = inputValue.substring(1);
        fetch('/search/users?usernamePrefix=' + searchValue)
            .then(response => response.json())
            .then(data => {
                chatList.innerHTML = '';
                displayChatsAndUsersList(data.chatsList);
                displayChatsAndUsersList(data.usersList);
            })
            .catch(error => console.error(error));
    }
    // else if (inputValue === ''){
    //     chatList.innerHTML = '';
    //     xhr.onreadystatechange = function() {
    //         if (this.readyState === 4 && this.status === 200) {
    //             const response = JSON.parse(this.responseText);
    //             displayChatsAndUsersList(response.chatsList);
    //         }
    //     };
    //     xhr.open('GET', '/api/chats', true);
    //     xhr.send();
    // }
});

emojiContainer.addEventListener('mouseover', function() {
    if (chatId != null) {
        emojiMenu.style.display = 'block';
    }
});

emojiMenu.addEventListener('mouseleave', function() {
    emojiMenu.style.display = 'none';
});

emojiItems.forEach(item => {
    item.addEventListener('click', () => {
        const emoji = item.getAttribute('data-emoji');
        messageInput.value += emoji;
    });
});