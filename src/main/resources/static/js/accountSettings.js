const navButtonUserInfo = document.querySelector(".nav-item-user");
const navButtonSecurity = document.querySelector(".nav-item-security");
const userDetailsBlock = document.querySelector(".user-details-block");
const userDetailsBlockSecurity = document.querySelector(".user-details-block-security")


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