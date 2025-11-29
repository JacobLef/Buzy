document.getElementById("loginForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const errorMessage = document.getElementById("errorMessage");

    errorMessage.textContent = "";

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        if (!response.ok) {
            errorMessage.textContent = "Invalid email or password.";
            return;
        }

        const data = await response.json();

        // save token & user info
        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);
        localStorage.setItem("email", data.email);
        localStorage.setItem("userId", data.userId);
        localStorage.setItem("businessPersonId", data.businessPersonId);

        //successful login -> turn to dash
        window.location.href = "/dashboard.html";

    } catch (error) {
        console.error(error);
        errorMessage.textContent = "Network error. Please try again.";
    }
});
