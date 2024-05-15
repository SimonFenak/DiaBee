import requests
from bs4 import BeautifulSoup
import json

while True:
    kategoria = input()
    # Načítanie obsahu stránky
    url = f"https://www.sacharidydovrecka.sk/kategoria/{kategoria}"
    response = requests.get(url)
    soup = BeautifulSoup(response.content, "html.parser")

    # Nájdenie tabuľky s údajmi
    table = soup.find("table")

    # Inicializácia prázdneho zoznamu pre údaje
    data = []

    # Prechádzanie riadkov tabuľky
    for row in table.find_all("tr"):
        # Inicializácia prázdneho zoznamu pre jednotlivé bunky riadku
        row_data = []
        # Prechádzanie buniek riadku
        for cell in row.find_all(["th", "td"]):
            # Pridanie textu z bunky do zoznamu
            row_data.append(cell.get_text(strip=True))
        # Pridanie riadku do zoznamu údajov
        data.append(row_data)

    # Uloženie údajov do JSON súboru
    with open(f"{kategoria}.json", "w", encoding="utf-8") as json_file:
        json.dump(data, json_file, ensure_ascii=False, indent=4)

    print("Údaje boli úspešne uložené do súboru 'sacharidy.json'.")
