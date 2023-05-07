const xhr = new XMLHttpRequest();
const chatList = document.getElementById('chat-list');
const sendMessageButton = document.getElementById("send-message-button");
const messageInput = document.getElementById('message-input')
const searchInput = document.getElementById("chats-search-input");
const currentDate = new Date();
let secondInterlocutorUniqueUsername;
let chatId;


function displayChatMessages(messages) {
    const chatMessages = document.getElementById('messages-chat');
    let lastSenderId = -1;
    chatMessages.innerHTML = '';

    for (let messageNumber = 0; messageNumber < messages.length; messageNumber++){
        let message = messages[messageNumber];
        const messageElement = document.createElement('div');

        if (lastSenderId !== message.senderId){
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

            lastSenderId = message.senderId;
            const messageTextBlock = messageElement.querySelector(".message-text");

            for (let j = messageNumber + 1; j < messages.length; j++, messageNumber++){
                if (lastSenderId === messages[j].senderId){
                    const messageTextParagraph = document.createElement('p');
                    messageTextParagraph.textContent =  messages[j].text;
                    messageTextBlock.appendChild(messageTextParagraph);
                }else {
                    break;
                }
            }
        }
        chatMessages.appendChild(messageElement);
    }
}

function displayChatsAndUsersList(chatsData) {
    chatsData.forEach(chat => {
        const chatDiv = document.createElement('div');
        chatDiv.classList.add('chat-list', 'discussion', 'message-active');
        chatDiv.setAttribute('data-chat-id', chat.chatId);
        chatDiv.innerHTML = `
                        <div class="photo" style="background-image: url(https://images.unsplash.com/photo-1438761681033-6461ffad8d80?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80);">
                            <div class="online"></div>
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

    chatId = event.target.closest('[data-chat-id]').dataset.chatId;
    secondInterlocutorUniqueUsername = event.target.closest('[data-chat-id]').querySelector(".unique-username").innerText.trim();
    // Отправка AJAX-запроса на сервер для получения истории сообщений
    xhr.open('GET', `/chats/${chatId}/messages`);
    xhr.onload = () => {
        const response = JSON.parse(xhr.responseText);

        const chatTitle = document.getElementById('chat-title');
        chatTitle.textContent = event.target.closest('[data-chat-id]').querySelector(".name").innerText;

        displayChatMessages(response.messages)

        // const chatDisplay = document.getElementById('chat-display');
        // chatDisplay.style.display = 'block';
        // chatList.style.display = 'none';
    };
    xhr.send();
});

sendMessageButton.addEventListener('click', event => {
    if (chatId === '0'){
        const data = {
            lastMessage: messageInput.value,
            createdAt: currentDate.toISOString(),
            membersNumber: 2,
        }
        xhr.open('POST', "api/chats/" + secondInterlocutorUniqueUsername.substring(1), false);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onload = function () {
            const response = JSON.parse(xhr.responseText);
            chatId = response.currentChatId;
            chatList.innerHTML = '';
            displayChatsAndUsersList(response.chatsList);
        };
        xhr.send(JSON.stringify(data));
    }

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
        fetch('/search/users?username=' + searchValue)
            .then(response => response.json())
            .then(data => {
                chatList.innerHTML = '';
                displayChatsAndUsersList(data.chatsList);
                displayChatsAndUsersList(data.usersList);
            })
            .catch(error => console.error(error));
    }else if (inputValue === ''){
        chatList.innerHTML = '';
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (this.readyState === 4 && this.status === 200) {
                const response = JSON.parse(this.responseText);
                displayChatsAndUsersList(response.chatsList);
            }
        };
        xhr.open('GET', '/api/chats', true);
        xhr.send();
    }
});