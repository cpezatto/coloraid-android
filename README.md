ColorAid (Android Version)
ColorAid é um aplicativo Android que ajuda pessoas daltônicas a identificar cores com precisão. Ele utiliza uma paleta de cores nomeadas (em diferentes idiomas e estilos) e detecta, com alta acurácia, a cor mais próxima de um pixel ou região de uma imagem.

✨ Funcionalidades
📷 Seleção de imagens da galeria

🎯 Toque para detectar a cor de um único pixel (com cruz central)

📐 Visualização do ponto tocado com coordenadas e valor RGB

🌍 Detecção multilíngue: nomes de cores em português, espanhol e inglês

🧩 Paletas múltiplas: basic, standard, web e xkcd (curadoria especial com nomes perceptivos e graciosos)

🔀 Detecção baseada em Delta E (CIELAB) para maior precisão perceptiva

🌐 Sistema de internacionalização via strings.xml

📁 Paletas de cores armazenadas em arquivos .json dentro de /assets/

🧠 Ideias em desenvolvimento
🧪 Seletor de paleta: o usuário poderá escolher entre diferentes paletas (ex: Web Colors, XKCD, etc.)

🎚️ Controle de tolerância (manual ou automático) para modo fuzzy

🔎 Zoom/magnifier para seleção precisa

💬 Possibilidade do usuário editar ou nomear cores com nomes próprios ("azul calcinha", por exemplo)

🧠 Sugestões inteligentes de nomes com base em percepções comuns

💾 Histórico e favoritos (versão premium)

🛍️ Versão premium com recursos extras (zoom, área livre, persistência, etc.)

📁 Paletas Disponíveis
🎨 Basic – Cores fundamentais facilmente reconhecíveis (disponível em pt, es, en)

🖌️ Standard – Cores expandidas comuns na comunicação visual (disponível em pt, es, en)

🌐 Web (CSS) – Cores padrão CSS/Web (disponível apenas em en)

💡 XKCD – Nomes criativos e perceptivos (extraídos da pesquisa do site XKCD, disponível apenas em en por enquanto)

📦 Estrutura do Projeto (Android)
perl
Copiar
Editar
app/
├── src/
│   └── main/
│       ├── java/com.claudio.coloraid/
│       │   ├── ui/             # Composables e tela principal
│       │   ├── viewmodel/      # ViewModel (MVVM)
│       │   ├── data/           # Lógica de paletas e loaders
│       │   ├── domain/         # Casos de uso e regras de negócio
│       └── assets/             # Paletas de cores em JSON
│       └── res/
│           ├── values/         # strings.xml (en)
│           ├── values-pt/      # strings.xml (pt)
│           └── values-es/      # strings.xml (es)
🧪 Tecnologias
Kotlin + Jetpack Compose

Android Studio (Ubuntu)

MVVM Architecture

Jackson (JSON parser)

CIELAB + Delta E color distance

GitHub for versioning

🚀 Como Executar
Clonar o repositório:

bash
Copiar
Editar
git clone https://github.com/cpezatto/coloraid-android.git
Abrir no Android Studio.

Sincronizar Gradle e dar run no app.

🌐 Internacionalização
Todos os textos da UI foram migrados para strings.xml, com suporte inicial para:

🇬🇧 English (values/)

🇪🇸 Español (values-es/)

🇧🇷 Português (values-pt/)

🧭 Próximos Passos
Implementar enum PaletteType e seletor de paleta

Interface para troca de idioma manual

Modo offline com cache das últimas cores detectadas

Exportar imagens com a cor detectada destacada

Feedback sonoro (voz) para leitura da cor (acessibilidade)

Permitir o usuário nomear cores e salvar no app

📜 Licença
MIT (a confirmar)

🙌 Autor
Claudio Pezatto · 2025
