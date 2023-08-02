// import axios from 'axios';

// import { Client } from '@stomp/stompjs';

const xhr = new XMLHttpRequest();
const editButton = document.querySelector('.edit-button');
const saveButton = document.querySelector('.save-button');
const cancelButton = document.querySelector('.cancel-button');
const userName = document.querySelector('.user-name');
const userDescription = document.querySelector('.user-description');
const userAvatar = document.querySelector(".user-avatar");
const userAvatarImg = document.querySelector(".user-img");
const userBgImage = document.querySelector(".user-info");
const imageInput = document.getElementById('image-input');
const copyLinkButton = document.querySelector(".copy-link-button");

//компоненты меню для загрузки и удаления аватара
const avatarUploadButton = document.querySelector("#upload-avatar-image-btn")
const avatarDeletedButton = document.querySelector("#delete-avatar-image-btn");
const avatarUploadMenu = document.querySelector('#avatar-image-options');

//компоненты меню для загрузки и удаления фонового изображения профиля
const bgImageUploadButton = document.querySelector("#upload-bg-image-btn")
const bgImageDeletedButton = document.querySelector("#delete-bg-image-btn");
const bgImageUploadMenu = document.querySelector('#bg-image-options');

let typeOfUploadedOrDeletedImage = null;

const userNameInput = document.createElement('input');
userNameInput.className = "user-details-text-input";

const userDescriptionInput = document.createElement('input');
userDescriptionInput.className = "user-details-text-input";

// const stompClient = new Client({
//     brokerURL: 'ws://localhost:9090/ws',
//     onConnect: () => {
//         stompClient.subscribe('/topic/chats.f488.participants.join', (message) => {
//             console.log('Получено сообщение:', message.body);
//         });
//     }
// });
//
// stompClient.activate();

const socket = new WebSocket('ws://localhost:9090/ws');
// const stompClient = webstomp.client('ws://localhost:9090/ws');
const stompClient = webstomp.over(socket);
stompClient.debug = () => {};

stompClient.connect({}, (frame) => {
    console.log('Connected to WebSocket server');

    // stompClient.subscribe('/topic/chats.f488.participants.join', (message) => {
    //     console.log('Получено сообщение:', message.body);
    // });
}, (error) => {
    console.error('Error connecting to WebSocket server:', error);
});


// stompClient.connect({}, (frame) => {
//     stompClient.send({
//         destination: '/topic/chats.f488.participants.join',
//         body: 'Привет, это сообщение от клиента!',
//     });
// })

function hideElement(element) {
    element.style.display = 'none';
}

function showElement(element) {
    element.style.display = '';
}

userBgImage.addEventListener('click', (event) => {
    if (avatarUploadMenu && editButton.style.display === 'none') {

        let x = event.clientX + 20;
        let y = event.clientY + 10;

        bgImageUploadMenu.style.left = x + 'px';
        bgImageUploadMenu.style.top = y + 'px';

        bgImageUploadMenu.style.display = 'block';

        typeOfUploadedOrDeletedImage = 'profileBgImage';
    }
});

userBgImage.addEventListener('mouseleave', () => {
    if (avatarUploadMenu) {
        bgImageUploadMenu.style.display = 'none';
        typeOfUploadedOrDeletedImage = null;
    }
})

bgImageUploadButton.addEventListener('click', () => {
    imageInput.click();
});

avatarUploadButton.addEventListener('click', () => {
   imageInput.click();
});

avatarDeletedButton.addEventListener('click', async () => {
    try {
        const response = await axios.delete('/image?typeImage=' + typeOfUploadedOrDeletedImage);
        const src = response.data;
        userAvatarImg.setAttribute('src', src);
    }catch (error) {
        console.log(error);
    }
});

imageInput.addEventListener('change', async (event) => {
    const file = event.target.files[0];
    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await axios.post('/image/upload?typeImage=' + typeOfUploadedOrDeletedImage, formData);
        const src = response.data;
        if (typeOfUploadedOrDeletedImage === 'avatar'){
            userAvatarImg.setAttribute('src', src);
        }else if (typeOfUploadedOrDeletedImage === 'profileBgImage'){
            userBgImage.style.background = `url('${src}')`;
        }else {
            console.error("typeOfUploadedOrDeletedImageError");
        }
    } catch (error) {
        console.error(error);
    }
});

userAvatar.addEventListener('mouseover', () => {
    typeOfUploadedOrDeletedImage = "avatar"
    if (avatarUploadMenu && editButton.style.display === "none") avatarUploadMenu.style.display = 'block';
});

userAvatar.addEventListener('mouseout', () => {
    typeOfUploadedOrDeletedImage = null;
    if (avatarUploadMenu) avatarUploadMenu.style.display = 'none';
});

function replaceElement(oldElement, newElement) {
    oldElement.parentNode.replaceChild(newElement, oldElement);
}

editButton.addEventListener('click', () => {
    // скрываем кнопку "Edit" и показываем кнопки "Save" и "Cancel"
    hideElement(editButton);
    showElement(saveButton);
    showElement(cancelButton);

    userNameInput.value = userName.textContent;
    userDescriptionInput.value = userDescription.textContent;

    replaceElement(userName, userNameInput);
    replaceElement(userDescription, userDescriptionInput);
});

saveButton.addEventListener('click', async () => {
    // получаем значения из полей ввода и заменяем ими элементы "h3" и "p"
    const userNameText = userNameInput.value;
    const userDescriptionText = userDescriptionInput.value;

    userName.textContent = userNameText;
    userDescription.textContent = userDescriptionText;

    try {
        const userData = {
            userName: userNameText,
            userDescription: userDescriptionText,
        };
        const response = await axios.post('/user/info', userData);
    }catch (error) {
        console.log('updateUserDetailsInfoError' + error);
    }

    replaceElement(userNameInput, userName);
    replaceElement(userDescriptionInput, userDescription);

    // показываем кнопку "Edit", скрываем кнопки "Save" и "Cancel"
    showElement(editButton);
    hideElement(saveButton);
    hideElement(cancelButton);
});

cancelButton.addEventListener('click', () => {
    // восстанавливаем исходные значения элементов "h3" и "p"
    replaceElement(userNameInput, userName);
    replaceElement(userDescriptionInput, userDescription);

    // показываем кнопку "Edit", скрываем кнопки "Save" и "Cancel"
    showElement(editButton);
    hideElement(saveButton);
    hideElement(cancelButton);
});

copyLinkButton.addEventListener("click", () => {
    const userId = copyLinkButton.dataset.userId;
    navigator.clipboard.writeText(window.location.protocol + window.location.host + `/home/${userId}`)
        .then(() => {
            copyLinkButton.style.backgroundColor = "#3d415a";
            setTimeout(function () {
                copyLinkButton.style.backgroundColor = "#007bff";
            }, 250);
        }).catch(() => {
            console.log("Error copy link")
        });
})