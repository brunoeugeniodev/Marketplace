#!/bin/bash
# Script simples para sincronizar arquivos sem Maven

echo "🔄 ===== SINCRONIZANDO ARQUIVOS ====="
echo ""

# Criar diretórios se não existirem
mkdir -p target/classes/static/scripts
mkdir -p target/classes/templates

# Copiar arquivos JavaScript
echo "📝 Copiando arquivos JavaScript..."
cp -f src/main/resources/static/scripts/header.js target/classes/static/scripts/header.js
cp -f src/main/resources/static/scripts/main.js target/classes/static/scripts/main.js
cp -f src/main/resources/static/scripts/carrinho.js target/classes/static/scripts/carrinho.js
cp -f src/main/resources/static/scripts/home.js target/classes/static/scripts/home.js
cp -f src/main/resources/static/scripts/loja-individual.js target/classes/static/scripts/loja-individual.js
echo "   ✅ JavaScript sincronizado"

# Copiar templates HTML
echo "📄 Copiando templates HTML..."
cp -f src/main/resources/templates/cabecalho.html target/classes/templates/cabecalho.html
cp -f src/main/resources/templates/loja.html target/classes/templates/loja.html
cp -f src/main/resources/templates/carrinho.html target/classes/templates/carrinho.html
echo "   ✅ Templates sincronizados"

# Verificar
echo ""
echo "✅ SINCRONIZAÇÃO CONCLUÍDA!"
echo ""
echo "📊 Verificação rápida:"
echo "   - Listeners no header.js: $(grep -c "addEventListener('click'" target/classes/static/scripts/header.js 2>/dev/null)"
echo "   - Listeners no main.js: $(grep -c "addEventListener('click'" target/classes/static/scripts/main.js 2>/dev/null)"
echo "   - Referências a placeholder.png: $(grep -r "placeholder.png" target/classes/static/scripts/ 2>/dev/null | wc -l)"
echo ""
echo "🔄 Reinicie o Spring Boot se estiver rodando"
echo "🌐 Limpe o cache do navegador (Ctrl+Shift+R)"

