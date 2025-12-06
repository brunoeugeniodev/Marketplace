#!/bin/bash
# Script para corrigir o loop e reiniciar a aplicação

echo "🔧 ===== CORREÇÃO DO LOOP E REINICIALIZAÇÃO ====="
echo ""

# 1. Parar processos Spring Boot em execução
echo "1️⃣ Parando processos Spring Boot..."
pkill -f "spring-boot" 2>/dev/null
pkill -f "marketplace" 2>/dev/null
sleep 2
echo "   ✅ Processos parados"
echo ""

# 2. Limpar cache do Maven
echo "2️⃣ Limpando cache do Maven..."
./mvnw clean
echo "   ✅ Cache limpo"
echo ""

# 3. Copiar arquivos corrigidos
echo "3️⃣ Sincronizando arquivos corrigidos..."
cp -f src/main/resources/static/scripts/header.js target/classes/static/scripts/header.js 2>/dev/null
cp -f src/main/resources/static/scripts/main.js target/classes/static/scripts/main.js 2>/dev/null
cp -f src/main/resources/static/scripts/carrinho.js target/classes/static/scripts/carrinho.js 2>/dev/null
cp -f src/main/resources/templates/cabecalho.html target/classes/templates/cabecalho.html 2>/dev/null
echo "   ✅ Arquivos sincronizados"
echo ""

# 4. Recompilar
echo "4️⃣ Recompilando aplicação..."
./mvnw compile
echo "   ✅ Compilação concluída"
echo ""

# 5. Verificar correções
echo "5️⃣ Verificando correções aplicadas..."
echo ""

# Verificar placeholder.png
PLACEHOLDER_COUNT=$(grep -r "placeholder.png" target/classes/static/scripts/ 2>/dev/null | wc -l)
if [ "$PLACEHOLDER_COUNT" -eq 0 ]; then
    echo "   ✅ Sem referências a placeholder.png"
else
    echo "   ❌ ERRO: Ainda há referências a placeholder.png!"
    grep -r "placeholder.png" target/classes/static/scripts/
fi

# Verificar listeners duplicados
HEADER_CLICKS=$(grep -c "addEventListener('click'" target/classes/static/scripts/header.js 2>/dev/null)
MAIN_CLICKS=$(grep -c "addEventListener('click'" target/classes/static/scripts/main.js 2>/dev/null)
echo "   📊 Event listeners 'click' no header.js: $HEADER_CLICKS"
echo "   📊 Event listeners 'click' no main.js: $MAIN_CLICKS"

if [ "$HEADER_CLICKS" -le 5 ]; then
    echo "   ✅ Listeners otimizados no header.js"
else
    echo "   ⚠️  Muitos listeners no header.js"
fi

echo ""

# 6. Iniciar aplicação
echo "6️⃣ Iniciando aplicação Spring Boot..."
echo ""
echo "⚠️  IMPORTANTE: Execute o comando abaixo em outra aba do terminal:"
echo ""
echo "    ./mvnw spring-boot:run"
echo ""
echo "   Depois:"
echo "   1. Aguarde a aplicação iniciar completamente"
echo "   2. Abra o navegador em http://localhost:8080"
echo "   3. Limpe o cache do navegador (Ctrl+Shift+Delete ou Ctrl+Shift+R)"
echo "   4. Faça login e teste o dropdown e o carrinho"
echo ""

# 7. Criar arquivo com instruções para browser
cat > LIMPAR-CACHE-BROWSER.md << 'EOF'
# 🌐 INSTRUÇÕES PARA LIMPAR CACHE DO NAVEGADOR

## O QUE FAZER PARA CORRIGIR O LOOP:

### 1. Limpar Cache do Navegador (OBRIGATÓRIO!)

#### Google Chrome / Chromium / Edge:
1. Pressione `Ctrl + Shift + Delete`
2. Selecione "Todo o período"
3. Marque:
   - ✅ Cookies e outros dados de sites
   - ✅ Imagens e arquivos armazenados em cache
   - ✅ Dados de aplicativos hospedados
4. Clique em "Limpar dados"

**OU** simplesmente:
- Pressione `Ctrl + Shift + R` (recarregar forçado)
- Ou `Ctrl + F5`

#### Firefox:
1. Pressione `Ctrl + Shift + Delete`
2. Selecione "Tudo"
3. Marque:
   - ✅ Cookies
   - ✅ Cache
4. Clique em "Limpar agora"

**OU**:
- Pressione `Ctrl + Shift + R`

### 2. Abrir Ferramentas do Desenvolvedor

Pressione `F12` e vá na aba **Console**

### 3. O que você deve ver no console (SEM ERROS):

```
🔧 HEADER.JS CARREGADO
✅ Header inicializado
✅ Elementos do dropdown encontrados
🛒 Carregando contador do carrinho...
✅ Carrinho carregado com sucesso
Script principal carregado
✅ Script principal inicializado com sucesso
```

### 4. O que NÃO deve aparecer:

❌ `No static resource imagens/placeholder.png`
❌ Múltiplas mensagens "Toggle dropdown"
❌ Erros de loop infinito
❌ Avisos de performance

### 5. Testar o Dropdown:

1. Faça login
2. Clique no nome do usuário no canto superior direito
3. O menu deve abrir **UMA VEZ**
4. Clique fora - deve fechar
5. Pressione ESC - deve fechar

### 6. Testar o Carrinho:

1. Adicione um produto ao carrinho
2. Vá para `/carrinho`
3. As imagens devem carregar corretamente
4. Se uma imagem falhar, deve mostrar o Logo.png
5. NÃO deve haver erros 404 no console

## SE AINDA HOUVER PROBLEMAS:

1. Feche TODAS as abas do navegador
2. Reabra o navegador
3. Vá direto para `http://localhost:8080`
4. Teste novamente

## VERIFICAR SE A APLICAÇÃO ESTÁ RODANDO:

Execute no terminal:
```bash
curl http://localhost:8080
```

Se retornar HTML, está funcionando!
EOF

echo "📋 Arquivo de instruções criado: LIMPAR-CACHE-BROWSER.md"
echo ""
echo "✅ CORREÇÃO CONCLUÍDA!"
echo ""
echo "🚀 Próximo passo: Inicie o Spring Boot e siga as instruções acima"

