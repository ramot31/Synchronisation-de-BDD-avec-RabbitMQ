@echo off
echo ============================
echo TEST APPROCHE 1 (MULTI QUEUE)
echo ============================

cd /d D:\RabbitMQTP\rabbitMQ-synch

echo.
echo 1. DEMARRAGE HO CONSUMER (2 queues)
start cmd /k mvn exec:java "-Dexec.mainClass=com.tp2.HOConsumer"

timeout /t 10


echo.
echo 2. INSERT 3 LIGNES DANS BO1 (INDIVIDUEL)

"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p585 -D db_bo1 -e "INSERT INTO Product_Sales (Date, Region, Product, Qty, Cost, Amt, Tax, Total, is_synced) VALUES ('2024-04-01','East','Paper',10,12.95,129.5,9.07,138.57,0);"

"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p585 -D db_bo1 -e "INSERT INTO Product_Sales (Date, Region, Product, Qty, Cost, Amt, Tax, Total, is_synced) VALUES ('2024-04-02','West','Pen',20,2.10,42.0,2.94,44.94,0);"

"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p585 -D db_bo1 -e "INSERT INTO Product_Sales (Date, Region, Product, Qty, Cost, Amt, Tax, Total, is_synced) VALUES ('2024-04-03','North','Book',5,15.00,75.0,5.25,80.25,0);"


timeout /t 5


echo.
echo 3. DEMARRAGE BO1 PRODUCER
start cmd /k mvn exec:java "-Dexec.mainClass=com.tp2.BO1Producer"

timeout /t 10


echo.
echo 4. INSERT 3 LIGNES DANS BO2 (INDIVIDUEL)

"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p585 -D db_bo2 -e "INSERT INTO Product_Sales (Date, Region, Product, Qty, Cost, Amt, Tax, Total, is_synced) VALUES ('2024-05-01','East','Paper',33,12.95,427.35,29.91,457.26,0);"

"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p585 -D db_bo2 -e "INSERT INTO Product_Sales (Date, Region, Product, Qty, Cost, Amt, Tax, Total, is_synced) VALUES ('2024-05-02','West','Pen',40,2.19,87.6,6.13,93.73,0);"

"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p585 -D db_bo2 -e "INSERT INTO Product_Sales (Date, Region, Product, Qty, Cost, Amt, Tax, Total, is_synced) VALUES ('2024-05-03','North','Folder',10,3.0,30.0,2.1,32.1,0);"


timeout /t 5


echo.
echo 5. DEMARRAGE BO2 PRODUCER
start cmd /k mvn exec:java "-Dexec.mainClass=com.tp2.BO2Producer"

timeout /t 5


echo.
echo 6. INSERT NOUVELLES LIGNES BO1

"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p585 -D db_bo1 -e "INSERT INTO Product_Sales (Date, Region, Product, Qty, Cost, Amt, Tax, Total, is_synced) VALUES ('2024-04-04','East','Mouse',8,10.0,80.0,5.6,85.6,0);"


timeout /t 5


echo.
echo 7. RELANCE BO1 PRODUCER
start cmd /k mvn exec:java "-Dexec.mainClass=com.tp2.BO1Producer"

timeout /t 5


echo.
echo 8. INSERT NOUVELLES LIGNES BO2

"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p585 -D db_bo2 -e "INSERT INTO Product_Sales (Date, Region, Product, Qty, Cost, Amt, Tax, Total, is_synced) VALUES ('2024-05-04','South','Mouse',15,10.0,150.0,10.5,160.5,0);"


timeout /t 5


echo.
echo 9. RELANCE BO2 PRODUCER
start cmd /k mvn exec:java "-Dexec.mainClass=com.tp2.BO2Producer"


echo.
echo ============================
echo TEST TERMINE
echo ============================

pause