# TripExpensesCalculator

Android alkalmazás, ami számontartja hogy egy túra alatt ki mennyit fizetett, és kiszámolja hogy a túra végén ki kinek mennyit fizessen hogy kiegyenlítődjenek a tartozások.

## Képernyőképek

<img src="https://github.com/kcsombi/TripExpensesCalculator/blob/master/Screenshots/Screenshot_2018-02-23-19-56-24.png" alt="alt text" width="200"> <img src="https://github.com/kcsombi/TripExpensesCalculator/blob/master/Screenshots/Screenshot_2018-02-23-19-56-27.png" alt="alt text" width="200">
<img src="https://github.com/kcsombi/TripExpensesCalculator/blob/master/Screenshots/Screenshot_2018-02-23-19-50-32.png" alt="alt text" width="200">

<img src="https://github.com/kcsombi/TripExpensesCalculator/blob/master/Screenshots/Screenshot_2018-02-23-19-48-32.png" alt="alt text" width="200"> <img src="https://github.com/kcsombi/TripExpensesCalculator/blob/master/Screenshots/Screenshot_2018-02-23-19-49-15.png" alt="alt text" width="200">
<img src="https://github.com/kcsombi/TripExpensesCalculator/blob/master/Screenshots/Screenshot_2018-02-23-19-57-11.png" alt="alt text" width="200">

## Használat
- **nézet váltás:** android drawer segítségével. Fő nézetre a vissza gombbal lehet navigálni
### Túrák
- **túra létrehozása:** Trips nézetben a "+" ikon megérintésével
- **túra aktiválása:** Trips nézetben az ACTIVATE gomb megérintése a megfelelő túrán
- **név módosítása:** Trips nézetben a túra nevének megérintésével
- **túra eltávolítása:** Trips nézetben a REMOVE gomb megérintése a törlendő túrán
### Személyek
- **személy hozzáadása:** Persons nézetben a "+" ikon megérintésével
- **név módosítása:** Persons nézetben az aktuális személy nevének megérintésével
- **személy törlése:** Persons nézetben az aktuális személyhez tartozó REMOVE gomb megérintésével. Csak akkor törlődik a személy, ha még nem vett részt fizetésben.
### Fizetések
- **fizetés hozzáadása:** Fő nézetben a "+" ikon megérintésével
    - **név választó:** Ki fizeti a vásárlást
    - **amount:** Fizetés összege
    - **comment:** Megjegyzés
    - **személyenkénti részletezés:**
        - **checkbox:** az aktuális személy részt vett-e a fizetésben
        - **beviteli mező:** Megadható hogy az aktuális személynek mennyit kellene fizetnie, illetve hogy mennyivel kellene többet vagy kevesebbet fizetnie mint a többieknek
        - **gomb:** Állapotot a gomb megérintésével lehet változtatni. Állapotok:
            - **" ":** A személynek annyit kellene fizetnie, amennyi a beviteli mezőben lévő érték
            - **"+":** A személynek annyival kellene többet fizetnie, amennyi a beviteli mezőben lévő érték
            - **"-":** A személynek annyival kellene kevesebbet fizetnie, amennyi a beviteli mezőben lévő érték
- **fizetés módosítása:** Payments nézetben az aktuális fizetés megérintésével
- **fizetés törlése:** Payments nézetben az aktuális fizetéshez tartozó REMOVE gomb megérintésével
