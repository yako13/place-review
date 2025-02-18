const emailInputEl = document.getElementById("email");

const emailVerificationButtonEl = document.getElementById("email-verification-btn");

const emailVerificationCodeContainerEl = document.getElementById("email-verification-code-con");

emailVerificationButtonEl.addEventListener("click", () => {

    const url = `${location.origin}/api/v1/verification/email`;
    const payload = {
        email: emailInputEl.value
    }

    sendJsonRequest(url, "post", payload).then(res => {
        emailVerificationCodeContainerEl.hidden = false;
    });
})

async function sendJsonRequest(url, method, payload) {
    return fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
}