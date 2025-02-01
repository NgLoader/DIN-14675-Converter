# DIN-14675 Converter

The **DIN-14675 Converter** is a command-line tool designed to export [BMA and SAA questions](https://www.dgwz.de/neue-pruefungsfragenkataloge-bma-saa-din-14675).
<br />
It then converts these questions into a JSON format that can easily be integrated into learning platforms such as [Repetico](https://repetico.de).

> **Note:** The converter does not handle images. If your questions include images, you will need to add them manually after conversion.

> **Note:** This tool will not answer the questions.

---

## Installation

1. **Clone the Repository:**

   Open your terminal and run:

   ```bash
   git clone https://github.com/NgLoader/DIN-14675-Converter.git
   cd DIN-14675-Converter
   ```

## Usage

1. **Prepare Your Input File:**

   Make sure you have a PDF formatted file containing the BMA and SAA questions you wish to convert.

2. **Run the Converter:**

   Use the command-line interface to run the converter. For example:

   ```bash
   java -jar DIN-14675-Converter.jar --input ./data/DIN14675_BMA.pdf --output ./data/export_BMA.json
   ```
   ```bash
   java -jar DIN-14675-Converter.jar --input ./data/DIN14675_SAA.pdf --output ./data/export_SAA.json
   ```

3. **Manual Image Addition:**

   If your questions require images, remember that you must add these manually to the JSON file or associate them later on your learning platform.

---

## Contributing

Contributions are very welcome! If you have suggestions, improvements, or bug fixes:

1. **Fork the repository.**
2. **Create a new branch** for your changes.
3. **Submit a pull request** with a clear description of your changes.

---

## License

This project is open-source and is distributed under the GPL-3.0 License. See the [LICENSE](LICENSE) file for more information.

---

For any questions or further issues, please open an issue in the GitHub repository.