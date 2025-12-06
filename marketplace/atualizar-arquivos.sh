#!/bin/bash

# ========================================
# SCRIPT PARA ATUALIZAR ARQUIVOS ESTÁTICOS
# ========================================

echo "🔄 Atualizando arquivos estáticos do Spring Boot..."

cd /home/Michino/Documents/Projects/NaLojaTem-main/marketplace

# Copiar JavaScript
echo "📁 Copiando arquivos JavaScript..."
cp -f src/main/resources/static/scripts/header.js target/classes/static/scripts/header.js
cp -f src/main/resources/static/scripts/main.js target/classes/static/scripts/main.js

# Copiar CSS
echo "📁 Copiando arquivos CSS..."
cp -f src/main/resources/static/estilos/style.css target/classes/static/estilos/style.css

# Copiar Templates
echo "📁 Copiando templates..."
cp -f src/main/resources/templates/cabecalho.html target/classes/templates/cabecalho.html

echo ""
echo "✅ TODOS OS ARQUIVOS ATUALIZADOS!"
echo ""
echo "📋 PRÓXIMOS PASSOS:"
echo "1. No navegador, pressione: Ctrl + Shift + Delete"
echo "2. Marque 'Imagens e arquivos em cache'"
echo "3. Clique em 'Limpar dados'"
echo "4. OU simplesmente: Ctrl + F5 (hard reload)"
echo "5. Recarregue a página"
echo ""
echo "🔍 Verifique no console se aparece:"
echo "   '🔧🔧🔧 HEADER.JS CARREGADO - VERSÃO ATUALIZADA 🔧🔧🔧'"
echo ""

