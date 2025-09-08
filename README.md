# 🛍️ Amazon Web Automation Framework

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Selenium](https://img.shields.io/badge/Selenium-WebDriver-green.svg)](https://selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-7.0+-blue.svg)](https://testng.org/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![Cucumber](https://img.shields.io/badge/Cucumber-7.0+-brightgreen.svg)](https://cucumber.io/)
[![ExtentReports](https://img.shields.io/badge/ExtentReports-5.0+-blueviolet.svg)](https://www.extentreports.com/)


A robust test automation framework designed to validate the functionality of the Amazon e-commerce website, built with Java, Selenium, TestNG, and Cucumber following industry best practices including **BDD** and **Page Object Model**.

---

## ✨ Features

### 🔍 **Product Search Testing**
- ✅ Valid search using search button and Enter key
- 🏷️ Department-specific search (Electronics, Books, etc.)
- 💡 Autocomplete suggestions validation
- ❌ Edge cases: empty, invalid, and extremely long queries

### 🔐 **User Authentication**
- ✅ Positive login flows with valid credentials
- ❌ Negative testing with invalid credentials
- 🔄 Session persistence validation across navigation

### 📝 **User Registration**
- ✅ Mandatory field validation
- 📋 Registration form error handling

### 🛒 **End-to-End Purchase Flow**
- 💳 Complete checkout process testing
- ❌ Negative scenarios with invalid payment details
- 🔍 Error handling validation

### 🏠 **Home Page UI Validation**
- 🎨 Critical UI elements verification (logo, banners)
- 🖼️ Broken images detection
- 🔗 Dead links identification

---

## 🛠️ Tech Stack

<table>
<tr>
<td align="center"><strong>Category</strong></td>
<td align="center"><strong>Technology</strong></td>
<td align="center"><strong>Purpose</strong></td>
</tr>
<tr>
<td>Programming Language</td>
<td>Java 11+</td>
<td>Core development language</td>
</tr>
<tr>
<td>Web Automation</td>
<td>Selenium WebDriver</td>
<td>Browser automation and interaction</td>
</tr>
<tr>
<td>Testing Framework</td>
<td>TestNG + Cucumber</td>
<td>Test execution and BDD scenarios</td>
</tr>
<tr>
<td>Build Tool</td>
<td>Apache Maven</td>
<td>Dependency management and build automation</td>
</tr>
<tr>
<td>Reporting</td>
<td>Extent Reports</td>
<td>Rich HTML test reports</td>
</tr>
<tr>
<td>Data Handling</td>
<td>Apache POI</td>
<td>Excel operations for test data</td>
</tr>
<tr>
<td>Driver Management</td>
<td>WebDriverManager</td>
<td>Automatic browser driver management</td>
</tr>
<tr>
<td>Logging</td>
<td>Log4j 2</td>
<td>Comprehensive logging system</td>
</tr>
</table>

---

## 📂 Project Structure

```
.
├── pom.xml                   # Maven Project Object Model
├── testng.xml                # TestNG suite for test execution
│
└── src
    └── test
        ├── java
        │   ├── base                # Core framework setup (DriverFactory, BaseTest)
        │   ├── pages               # Page Object Model classes (LoginPage, HomePage, etc.)
        │   ├── runners             # TestNG Cucumber runner
        │   ├── stepDefinitions     # Cucumber step definitions (glue code)
        │   └── utils               # Helper utilities (ConfigReader, ScreenshotUtils, etc.)
        │
        └── resources
            ├── features            # Cucumber .feature files
            ├── config.properties   # Main configuration file
            ├── log4j2.xml          # Logging configuration
            ├── extent-config.xml   # Extent Reports styling
            └── testdata.xlsx       # Test data for data-driven tests
```

---

## 🚀 Quick Start

### 📋 Prerequisites

<table>
<tr><td><strong>Requirement</strong></td><td><strong>Version</strong></td><td><strong>Download Link</strong></td></tr>
<tr><td>Java JDK</td><td>11 or higher</td><td><a href="https://adoptium.net/">adoptium.net</a></td></tr>
<tr><td>Apache Maven</td><td>3.6 or higher</td><td><a href="https://maven.apache.org/download.cgi">maven.apache.org</a></td></tr>
<tr><td>Git</td><td>Latest</td><td><a href="https://git-scm.com/">git-scm.com</a></td></tr>
</table>

### 📥 Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/anukul-hub/E-commerece-Capstone-project-amazon-automation.git
   cd E-commerece-Capstone-project-amazon-automation
   ```

2. **Install dependencies:**
   ```bash
   mvn clean install
   ```

3. **Verify installation:**
   ```bash
   mvn clean compile
   ```

---

## 🧪 Running Tests

### 🖥️ Command Line Execution

**Run all tests:**
```bash
mvn clean test
```

**Run specific test suite:**
```bash
mvn clean test -Dsurefire.suiteXmlFiles=testng.xml
```

**Run with specific browser:**
```bash
mvn clean test -Dbrowser=chrome
```

**Run with custom environment:**
```bash
mvn clean test -Denv=staging -Dbrowser=firefox
```

### 💻 IDE Execution

**IntelliJ IDEA / Eclipse:**
- Right-click on `testng.xml` → **Run**
- Right-click on any feature file → **Run Feature**
- Right-click on `TestRunner.java` → **Run**

---

## 📊 Test Reports

### 📈 **Extent Reports**
Rich, interactive HTML reports with detailed execution insights:

- **📍 Location:** `test-output/extent-report.html` 
- **✨ Features:**
  - Test execution dashboard
  - Step-by-step execution details
  - Embedded screenshots for failures
  - Execution timeline and statistics

### 🥒 **Cucumber Reports**
Standard BDD reports in multiple formats:

- **📍 Location:** `target/cucumber-reports/cucumber.html`
- **📋 Formats:** HTML, JSON

### 📸 **Screenshots**
Automatic screenshot capture on test failures:

- **📍 Location:** `test-outpt/screenshots/`
- **🎯 Naming:** `Scenario_TestName_Timestamp.png`

---

## ⚙️ Configuration

### 🔧 **config.properties**
Change the baseUrl, browser, and test user credentials here:
```properties
# Application Settings
baseUrl=https://www.amazon.in
browser=chrome
headless=false
explicitWait=25

# Test User Credentials
regMobile=your_mobile
regName=Anukul Chauhan
regPassword=tempPAss

# Reporting
reports.screenshots=true
reports.extent=true
```

### 📊 **testdata.xlsx**
Modify or add test data for your data-driven scenarios (e.g., add new products to search for):
- **Sheet 1:** Login credentials
- **Sheet 2:** Search keywords
- **Sheet 3:** Product details
- **Sheet 4:** Checkout information

### 🎯 **testng.xml**
Modify the test suite to run specific classes or pass parameters to your tests:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Amazon Automation Suite">
    <test name="Smoke Tests">
        <classes>
            <class name="runners.CucumberTestRunner"/>
        </classes>
    </test>
</suite>
```