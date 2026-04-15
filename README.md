# TP MySQL - Distributed Databases

## How to run

1. Open MySQL
2. Execute:
SOURCE create_databases.sql;
SOURCE create_tables.sql;
SOURCE insert_bo1.sql;
SOURCE insert_bo2.sql;


## Databases
- db_ho (Head Office)
- db_bo1 (Branch Office 1)
- db_bo2 (Branch Office 2)

## Notes

- Each branch has its own Product_Sales table
- Data is distributed across branch databases
- Scripts are separated for clarity and reuse

---

## Verification


USE db_bo1;
SELECT * FROM Product_Sales;

USE db_bo2;
SELECT * FROM Product_Sales;