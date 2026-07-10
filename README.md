# HTML Metadata Editor

Editor visual de metadados HTML construído em Java Swing. Permite carregar um arquivo HTML, visualizar e editar seus metadados (tags `<meta>`) e salvar as alterações de volta.

## Funcionalidades

- **Três categorias de metadados** — Standard (description, keywords, author, viewport, robots, charset), Open Graph (og:title, og:description, og:image, etc.) e Twitter Cards (twitter:card, twitter:site, etc.)
- **Propagação automática** — Ao preencher Title ou Description na aba Standard, os campos equivalentes em Open Graph e Twitter são preenchidos automaticamente se estiverem vazios
- **Metadados customizados** — Área "Outros" em cada aba para adicionar tags que não têm campo dedicado (formato `chave=valor` por linha)
- **Preserva o HTML** — Apenas as tags `<title>` e `<meta>` são alteradas; o resto do documento permanece intacto

## Requisitos

- Java 17+
- Maven 3.6+

## Como executar

```bash
mvn package -q
java -jar target/htmlmetadataeditor-1.0.jar
```

Ou diretamente com o Maven:

```bash
mvn compile
mvn exec:java  # requer plugin exec-maven-plugin
```

## Uso

1. Clique em **Selecionar** e escolha um arquivo `.html` ou `.htm`
2. Clique em **Carregar**
3. Edite os metadados nas abas **Standard**, **Open Graph** e **Twitter Cards**
4. Clique em **Salvar**

## Dependências

- [Jsoup](https://jsoup.org/) 1.19.1 — parsing e escrita de HTML
