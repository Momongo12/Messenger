const xhr = new XMLHttpRequest();
const navButtonUserInfo = document.querySelector(".nav-item-user");
const navButtonSecurity = document.querySelector(".nav-item-security");
const userDetailsBlock = document.querySelector(".user-details-block");
const userDetailsBlockSecurity = document.querySelector(".user-details-block-security")
const saveButtons = document.querySelectorAll(".save-button");


navButtonUserInfo.addEventListener('click', () => {
    userDetailsBlockSecurity.style.display = 'none';
    userDetailsBlock.style.display = 'flex';
    userDetailsBlockSecurity.classList.add('hidden')
    userDetailsBlock.classList.remove('hidden')
})

navButtonSecurity.addEventListener('click', () => {
    userDetailsBlockSecurity.style.display = 'flex';
    userDetailsBlock.style.display = 'none';
    userDetailsBlockSecurity.classList.remove('hidden')
    userDetailsBlock.classList.add('hidden')
})


saveButtons.forEach(saveButton => {
    saveButton.addEventListener('click', () => {
        const uniqueUsernameInput = document.querySelector("#unique_username");
        const uniqueUsername = uniqueUsernameInput.value.trim();

        if (uniqueUsername === '' || uniqueUsername.substring(1).trim() === ''){
            uniqueUsernameInput.style.backgroundColor = "rgb(247, 19, 19, 0.3)";
            setTimeout(() => {
                uniqueUsernameInput.style.backgroundColor = "";
            }, 2000);
            return;
        }

        const usernameInput = document.querySelector("#username");
        const emailInput = document.querySelector("#email");
        const phoneNumberInput = document.querySelector("#phone_number");
        const isShowingEmailCheckBox = document.querySelector(".is-showing-email");
        const isPublicProfileCheckBox = document.querySelector(".is-public-profile");

        const data = {
            username : usernameInput.value,
            email : emailInput.value,
            uniqueUsername: uniqueUsername.substring(1).trim(),
            phoneNumber : phoneNumberInput.value,
            isPublicProfile : isPublicProfileCheckBox.checked,
            isShowingEmail : isShowingEmailCheckBox.checked
        }

        xhr.open('PUT', "/settings");
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onload = function () {
            if (this.status !== 200) {
                uniqueUsernameInput.style.backgroundColor = "rgb(247, 19, 19, 0.3)";
                setTimeout(() => {
                    uniqueUsernameInput.style.backgroundColor = "";
                }, 2000);
            }else {
                document.querySelector("#title-username").textContent = usernameInput.value;
            }
        }
        xhr.send(JSON.stringify(data));
    });
});
