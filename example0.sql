USE personal_finance_management0;
-- 插入用户表记录
INSERT INTO `user` (`name`, `password`, `email`, `role`) 
VALUES 
('john_doe', MD5('password123'), 'john@example.com', '用户'),
('admin', MD5('admin123'), 'admin@example.com', '管理员');

-- 插入账户表记录
INSERT INTO `account` (`user_id`, `name`, `balance`) 
VALUES 
(1, 'Cash', 500.00),
(1, 'Bank Account', 1500.00),
(2, 'Credit Card', 2000.00);

-- 插入收入表记录
INSERT INTO `income` (`user_id`, `account_id`, `category`, `amount`, `description`, `date`) 
VALUES 
(1, 1, 'Salary', 3000.00, 'Monthly salary', '2023-10-01'),
(1, 2, 'Freelance', 500.00, 'Freelance project', '2023-10-05');

-- 插入支出表记录
INSERT INTO `expense` (`user_id`, `account_id`, `category`, `amount`, `description`, `date`) 
VALUES 
(1, 1, 'Groceries', 200.00, 'Weekly groceries', '2023-10-02'),
(1, 2, 'Rent', 1000.00, 'Monthly rent', '2023-10-03');

-- 插入预算表记录
INSERT INTO `budget` (`user_id`, `category`, `amount`, `start_date`, `end_date`) 
VALUES 
(1, 'Groceries', 800.00, '2023-10-01', '2023-10-31'),
(1, 'Rent', 1000.00, '2023-10-01', '2023-10-31');

-- 插入账单表记录
INSERT INTO `bill` (`user_id`, `account_id`, `amount`, `due_date`, `status`) 
VALUES 
(1, 2, 100.00, '2023-10-15', '未支付'),
(2, 3, 200.00, '2023-10-20', '已支付');

-- 插入备份表记录
INSERT INTO `backup` (`user_id`, `file_path`) 
VALUES 
(1, '/backups/user1_backup_20231001.sql'),
(2, '/backups/user2_backup_20231001.sql');