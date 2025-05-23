# 📱 SMS Phishing Protector

**SMS Phishing Protector** is a JVM-based system for detecting and blocking phishing SMS messages in real time. It was created as a prototype for a telecom operator that partnered with a banking association to protect subscribers from fraudulent links in text messages.

---

## ⚡ Why SMS Phishing Protector?

- **Production-Ready** – Designed for fast deployment and low maintenance costs
- **Cloud-Native** – Ready for Kubernetes, AWS, or standalone setups
- **Lightweight & Scalable** – Modular microservices with clean APIs
- **Cost-Effective** – Optimized use of paid external APIs via caching

---

## Architecture Overview

SMS Shield is composed of four microservices:

| Service                  | Description                                                                 |
|--------------------------|-----------------------------------------------------------------------------|
| **SMS Ingestion**        | Ingests and processes incoming/outgoing SMS in JSON format                  |
| **Phishing Control**     | Detects phishing URLs via Google Web Risk API with database and Redis cache |
| **User Service**         | Manages opt-in/out preferences via START/STOP SMS commands                  |
| **Notification Service** | Sends alerts or logs about blocked phishing attempts                        |

---
## System design 
https://excalidraw.com/#json=uTRvv02DayBuiJ_ckNrU8,T_dHKk-2EdqndFwAXe3oYg
## 🔍 How It Works

1. All incoming SMS messages are ingested in JSON format:
    ```json
    {
      "sender": "234100200300",
      "recipient": "48700800999",
      "message": "Visit https://www.m-bonk.pl.ng/personal-data to confirm your info."
    }
    ```

2. The `Phishing Control` service scans the message for URLs.
3. The system checks the URL:
    - First in Redis cache
    - Then in the internal database
    - If not found, calls the **Google Web Risk API**  
      ([API Reference](https://cloud.google.com/web-risk/docs/reference/rest/v1eap1/TopLevel/evaluateUri))
4. If the URL is malicious, the SMS is **blocked**.
5. Users opt-in or out by sending **START** or **STOP** to a predefined short number.
6. Actions are logged and optionally sent via the `Notification Service`.

---

## 🚀 Getting Started

### Clone and run with Docker:

```bash
git clone https://github.com/romenskd/SMS-phishing-protector
SMS-phishing-protector
docker-compose up -d

--- 
docker run -d --name user-service -p 8081:8080 romenskd/user-service
docker run -d --name sms-ingestion-service -p 8080:8080 romenskd/sms-ingestion-service
docker run -d --name phishing-control-service -p 8082:8080 romenskd/phishing-control-service
docker run -d --name notification-service -p 8083:8080 romenskd/notification-service
```

##  Future Plans

-  **AI-based phishing detection** – Smarter, adaptive threat analysis using machine learning  
-  **Web dashboard for logs and reports** – Real-time visibility into SMS filtering activity  
-  **Integrations with Telegram, Slack, etc.** – Alerting and operational notifications via popular channels
-  **AI-based phishing detection** – Smarter, adaptive threat analysis using machine learning
-  **Web dashboard for logs and reports** – Real-time visibility into SMS filtering activity
-  **Integrations with Telegram, Slack, etc.** – Alerting and operational notifications via popular channels
