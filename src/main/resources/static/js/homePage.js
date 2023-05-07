// import axios from 'axios';

const xhr = new XMLHttpRequest();
const editButton = document.querySelector('.edit-button');
const saveButton = document.querySelector('.save-button');
const cancelButton = document.querySelector('.cancel-button');
const userName = document.querySelector('.user-name');
const userDescription = document.querySelector('.user-description');
const userAvatar = document.querySelector(".user-avatar");
const userImg = document.querySelector(".user-img");
const imageInput = document.getElementById('image-input');
const uploadImageButton = document.querySelector(".upload-image-btn")
const deleteImageButton = document.querySelector(".delete-image-btn");
let typeOfUploadedOrDeletedImage = null;

const userNameInput = document.createElement('input');
userNameInput.className = "user-details-text-input";

const userDescriptionInput = document.createElement('input');
userDescriptionInput.className = "user-details-text-input";

function hideElement(element) {
    element.style.display = 'none';
}

function showElement(element) {
    element.style.display = '';
}

uploadImageButton.addEventListener('click', () => {
   imageInput.click();
});

deleteImageButton.addEventListener('click', async () => {
    try {
        const response = await axios.delete('/image?typeImage=' + typeOfUploadedOrDeletedImage);
        const src = response.data;
        userImg.setAttribute('src', src);
    }catch (error) {
        console.log(error);
    }
})

imageInput.addEventListener('change', async (event) => {
    const file = event.target.files[0];
    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await axios.post('/image/upload?typeImage=' + typeOfUploadedOrDeletedImage, formData);
        const src = response.data;
        userImg.setAttribute('src', src);
    } catch (error) {
        console.error(error);
    }
});

userAvatar.addEventListener('mouseover', () => {
    typeOfUploadedOrDeletedImage = "avatar"
    const uploadMenu = document.querySelector('.image-options');
    if (uploadMenu && editButton.style.display === "none") uploadMenu.style.display = 'block';
});

userAvatar.addEventListener('mouseout', () => {
    typeOfUploadedOrDeletedImage = null;
    const uploadMenu = document.querySelector('.image-options');
    if (uploadMenu) uploadMenu.style.display = 'none';
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

saveButton.addEventListener('click', () => {
    // получаем значения из полей ввода и заменяем ими элементы "h3" и "p"
    const userNameText = userNameInput.value;
    const userDescriptionText = userDescriptionInput.value;

    userName.textContent = userNameText;
    userDescription.textContent = userDescriptionText;

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
