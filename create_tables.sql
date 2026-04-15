USE db_ho;

CREATE TABLE Product_Sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    Date DATE NOT NULL,
    Region VARCHAR(50),
    Product VARCHAR(100) NOT NULL,
    Qty INT,
    Cost DECIMAL(10,2),
    Amt DECIMAL(10,2),
    Tax DECIMAL(10,2),
    Total DECIMAL(10,2)
);

USE db_bo1;
CREATE TABLE Product_Sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    Date DATE NOT NULL,
    Region VARCHAR(50),
    Product VARCHAR(100) NOT NULL,
    Qty INT,
    Cost DECIMAL(10,2),
    Amt DECIMAL(10,2),
    Tax DECIMAL(10,2),
    Total DECIMAL(10,2)
);

USE db_bo2;
CREATE TABLE Product_Sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    Date DATE NOT NULL,
    Region VARCHAR(50),
    Product VARCHAR(100) NOT NULL,
    Qty INT,
    Cost DECIMAL(10,2),
    Amt DECIMAL(10,2),
    Tax DECIMAL(10,2),
    Total DECIMAL(10,2)
);