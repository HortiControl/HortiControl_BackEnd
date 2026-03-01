# HortiControl_BackEnd

<div align="center">
  <strong>Modernização e Inovação Tecnológica Aplicadas à Gestão de Hortaliças🥬</strong>
</div>

</br>

<div align="center">
  <p align="justify">Este repositório contém o backend do projeto HortiControl, um sistema web desenvolvido como projeto acadêmico com o objetivo de apoiar a digitalização dos processos da empresa Alto Tietê Hortaliças. A solução foi idealizada para substituir fluxos operacionais manuais e descentralizados — atualmente realizados por meio de WhatsApp, anotações em papel e controles informais — por uma plataforma integrada capaz de centralizar e padronizar o cadastro de clientes e produtos, o registro e acompanhamento de pedidos, o controle de estoque, a emissão e organização de notas por entrega e o monitoramento de pagamentos realizados. O sistema organiza todo o ciclo operacional da empresa, desde o pedido até a cobrança, garantindo maior rastreabilidade das informações, redução de erros, diminuição de retrabalho e maior confiabilidade na gestão de dados. Desenvolvido como um MVP ao longo do semestre, o projeto aplica conceitos de engenharia de software, modelagem de processos e desenvolvimento web, além de propor uma solução escalável com potencial de expansão futura para outras empresas do setor hortifrúti que enfrentam desafios semelhantes no processo de transformação digital.</p>
</div>

</br>

<p align=center>

## ⚙️ Funcionalidades do sistema:

- Cadastro de clientes e produtos
- Registro e acompanhamento de pedidos
- Controle de estoque
- Histórico de vendas
- Dashboard gerencial com indicadores

</p>

</br>

<p align=center>

## 🔍 Visão Geral

O HortiControl é um sistema web interno desenvolvido como projeto acadêmico para digitalizar e organizar os processos da Alto Tietê Hortaliças. A plataforma centraliza o fluxo completo da operação — do pedido à cobrança — substituindo controles manuais e descentralizados por uma solução integrada que aumenta a rastreabilidade, reduz erros e fortalece a gestão administrativa e financeira da empresa.

</p>

## 🛠️ Tecnologias

- Java 11
- Spring Boot 2.7.18
- Spring Web
- Spring Data JPA + Hibernate
- H2 Database (banco em memória)

---

## ▶️ Como rodar

### Pré-requisitos

- Java 11+
- Maven 3.6+

### Passos

```bash
# Clone o repositório
git clone https://github.com/HortiControl/HortiControl_BackEnd.git

# Entre na pasta
cd HortiControl_BackEnd

# Suba a aplicação
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

---

## 📌 Endpoints disponíveis

Base URL: `http://localhost:8080/produtos`

| Método | Endpoint | Descrição | Status de retorno |
|--------|----------|-----------|-------------------|
| GET | `/produtos` | Lista todos os produtos | 200 / 204 |
| GET | `/produtos/{id}` | Busca produto por ID | 200 / 404 |
| POST | `/produtos` | Cria novo produto | 201 |
| PUT | `/produtos/{id}` | Atualiza produto existente | 200 / 404 |
| DELETE | `/produtos/{id}` | Remove produto por ID | 200 / 404 |

---
