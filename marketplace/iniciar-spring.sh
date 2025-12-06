#!/bin/bash
# Script para iniciar o Spring Boot de forma correta

echo "🚀 ===== INICIANDO SPRING BOOT ====="
echo ""

# 1. Sincronizar arquivos primeiro
echo "1️⃣ Sincronizando arquivos..."
./sincronizar.sh
echo ""

# 2. Verificar se a porta 8080 está em uso
echo "2️⃣ Verificando porta 8080..."
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "   ⚠️  Porta 8080 já está em uso!"
    echo "   Deseja parar o processo? (s/n)"
    read -r resposta
    if [ "$resposta" = "s" ] || [ "$resposta" = "S" ]; then
        echo "   Parando processo na porta 8080..."
        kill -9 $(lsof -ti:8080) 2>/dev/null
        sleep 2
        echo "   ✅ Processo parado"
    fi
else
    echo "   ✅ Porta 8080 está livre"
fi
echo ""

# 3. Verificar se existe pom.xml
echo "3️⃣ Verificando projeto Maven..."
if [ ! -f "pom.xml" ]; then
    echo "   ❌ ERRO: pom.xml não encontrado!"
    echo "   Execute este script na pasta raiz do projeto marketplace"
    exit 1
fi
echo "   ✅ Projeto Maven encontrado"
echo ""

# 4. Tentar diferentes formas de iniciar
echo "4️⃣ Iniciando Spring Boot..."
echo ""

if [ -x "mvnw" ]; then
    echo "   Usando Maven Wrapper (./mvnw)..."
    ./mvnw spring-boot:run
elif command -v mvn &> /dev/null; then
    echo "   Usando Maven global (mvn)..."
    mvn spring-boot:run
else
    echo "   ❌ ERRO: Nem mvnw nem mvn foram encontrados!"
    echo ""
    echo "   💡 SOLUÇÕES:"
    echo ""
    echo "   A) Se você usa IntelliJ IDEA:"
    echo "      1. Abra o projeto no IntelliJ"
    echo "      2. Encontre a classe principal (com @SpringBootApplication)"
    echo "      3. Clique com botão direito > Run"
    echo ""
    echo "   B) Se você usa Eclipse:"
    echo "      1. Abra o projeto no Eclipse"
    echo "      2. Right-click no projeto > Run As > Spring Boot App"
    echo ""
    echo "   C) Instalar Maven:"
    echo "      sudo pacman -S maven  # Arch Linux"
    echo "      sudo apt install maven  # Ubuntu/Debian"
    echo ""
    echo "   D) Usar o JAR compilado (se existir):"
    echo "      java -jar target/*.jar"
    echo ""
    exit 1
fi

