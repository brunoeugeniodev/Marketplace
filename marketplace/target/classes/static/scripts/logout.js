// static/js/logout.js
function logout() {
    console.log('Função logout chamada');

    fetch('/api/auth/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (response.ok) {
            // Limpar dados locais se existirem
            localStorage.removeItem('usuario');
            localStorage.removeItem('jwtToken');
            // Redireciona para home
            window.location.href = '/';
        }
    }).catch(error => {
        console.error('Erro no logout:', error);
        window.location.href = '/';
    });
}