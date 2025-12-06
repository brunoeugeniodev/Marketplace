// ========== LOJA INDIVIDUAL PAGE - Scripts específicos ==========

document.addEventListener('DOMContentLoaded', function() {
    console.log('Loja individual page - Script carregado');

    // ========== NAVEGAÇÃO DA LOJA ==========
    const storeNavLinks = document.querySelectorAll('.store-nav a');
    if (storeNavLinks.length > 0) {
        storeNavLinks.forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();

                // Atualizar link ativo
                storeNavLinks.forEach(l => l.classList.remove('active'));
                this.classList.add('active');

                const targetId = this.getAttribute('href').substring(1);
                const targetElement = document.getElementById(targetId);

                if (targetElement) {
                    // Scroll suave
                    window.scrollTo({
                        top: targetElement.offsetTop - 100,
                        behavior: 'smooth'
                    });
                }
            });
        });
    }

    // ========== SEGUIR LOJA ==========
    const btnFollow = document.querySelector('.btn-follow');
    if (btnFollow) {
        btnFollow.addEventListener('click', function() {
            const isFollowing = this.classList.contains('seguindo');
            const icon = this.querySelector('i');

            if (isFollowing) {
                this.classList.remove('seguindo');
                this.innerHTML = '<i class="far fa-heart"></i> Seguir Loja';
                showNotification('Loja removida dos favoritos', 'info');
            } else {
                this.classList.add('seguindo');
                this.innerHTML = '<i class="fas fa-heart"></i> Seguindo';
                showNotification('Loja adicionada aos favoritos', 'success');
            }

            // TODO: Integrar com API para seguir/parar de seguir
            const lojaId = window.location.pathname.split('/').pop();
            const action = isFollowing ? 'unfollow' : 'follow';

            fetch(`/api/lojas/${lojaId}/${action}`, {
                method: 'POST',
                headers: getAuthHeader()
            })
            .catch(error => {
                console.error('Erro ao seguir loja:', error);
                // Reverter visualmente se der erro
                if (isFollowing) {
                    this.classList.add('seguindo');
                    this.innerHTML = '<i class="fas fa-heart"></i> Seguindo';
                } else {
                    this.classList.remove('seguindo');
                    this.innerHTML = '<i class="far fa-heart"></i> Seguir Loja';
                }
            });
        });
    }

    // ========== CONTATAR LOJA ==========
    const btnContact = document.querySelector('.btn-contact');
    if (btnContact) {
        btnContact.addEventListener('click', function() {
            showNotification('Em breve implementaremos o sistema de contato!', 'info');
        });
    }

    // ========== ADICIONAR AO CARRINHO ==========
    const botoesAddCarrinho = document.querySelectorAll('.btn-add-cart');

    botoesAddCarrinho.forEach(botao => {
        botao.addEventListener('click', async function(e) {
            e.preventDefault();
            e.stopPropagation();

            const produtoId = this.getAttribute('data-produto-id');

            if (!produtoId) {
                console.error('ID do produto não encontrado');
                return;
            }

            // Verificar se está logado
            const token = localStorage.getItem('jwtToken');
            if (!token) {
                if (typeof window.showNotification === 'function') {
                    window.showNotification('Você precisa estar logado para adicionar produtos ao carrinho!', 'error');
                } else {
                    alert('Você precisa estar logado para adicionar produtos ao carrinho!');
                }
                setTimeout(() => {
                    window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname);
                }, 1500);
                return;
            }

            // Desabilitar botão durante a requisição
            const originalHtml = this.innerHTML;
            this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Adicionando...';
            this.classList.add('loading');
            this.disabled = true;

            try {
                const response = await fetch('/api/carrinho/itens', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        produtoId: parseInt(produtoId),
                        quantidade: 1
                    })
                });

                if (response.ok) {
                    const result = await response.json();

                    // Atualizar contador do carrinho usando função global
                    if (typeof window.atualizarContadorCarrinho === 'function') {
                        window.atualizarContadorCarrinho(result);
                    }

                    // Feedback visual
                    this.innerHTML = '<i class="fas fa-check"></i> Adicionado!';
                    this.style.background = '#2e7d32';

                    if (typeof window.showNotification === 'function') {
                        window.showNotification('✅ Produto adicionado ao carrinho!', 'success');
                    }

                    // Restaurar botão após 2 segundos
                    setTimeout(() => {
                        this.innerHTML = originalHtml;
                        this.style.background = '';
                        this.classList.remove('loading');
                        this.disabled = false;
                    }, 2000);

                } else {
                    const error = await response.json();
                    throw new Error(error.message || 'Erro ao adicionar produto ao carrinho');
                }

            } catch (error) {
                console.error('Erro ao adicionar ao carrinho:', error);

                if (typeof window.showNotification === 'function') {
                    window.showNotification(`❌ ${error.message}`, 'error');
                } else {
                    alert(`❌ ${error.message}`);
                }

                // Restaurar botão
                this.innerHTML = originalHtml;
                this.classList.remove('loading');
                this.disabled = false;
            }
        });
    });

    // ========== ORDENAÇÃO DE PRODUTOS ==========
    const selectOrdenacao = document.querySelector('select');
    if (selectOrdenacao) {
        selectOrdenacao.addEventListener('change', function() {
            const ordenacao = this.value;
            showNotification(`Ordenando por: ${this.options[this.selectedIndex].text}`, 'info');

            // TODO: Implementar ordenação real via AJAX
            console.log('Ordenação selecionada:', ordenacao);
        });
    }

    console.log('Loja individual page scripts inicializados');
});