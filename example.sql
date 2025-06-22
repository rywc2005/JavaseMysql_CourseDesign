-- 创建数据库
CREATE DATABASE personal_finance_management0 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; -- 创建名为 personal_finance_management 的数据库，使用 utf8mb4 字符集和排序规则

-- 使用数据库
USE personal_finance_management0; -- 选择使用刚创建的数据库

-- 创建用户表
CREATE TABLE `user` (
    `id` INT AUTO_INCREMENT PRIMARY KEY, -- 用户表的主键，自增
    `name` VARCHAR(50) NOT NULL UNIQUE, -- 用户名，最大长度50，唯一且不能为空
    `password` VARCHAR(255) NOT NULL, -- 用户密码，最大长度255，不能为空
    `email` VARCHAR(100) NOT NULL UNIQUE, -- 用户邮箱，最大长度100，唯一且不能为空
    `role` ENUM('用户', '管理员') DEFAULT '用户', -- 用户角色，默认为普通用户
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 用户创建时间，默认为当前时间
);

-- 创建账户表
CREATE TABLE `account` (
    `id` INT AUTO_INCREMENT PRIMARY KEY, -- 账户表的主键，自增
    `user_id` INT NOT NULL, -- 关联的用户ID，不能为空
    `name` VARCHAR(50) NOT NULL, -- 账户名称，最大长度50，不能为空
    `balance` DECIMAL(10, 2) DEFAULT 0.00, -- 账户余额，默认为0.00
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 账户创建时间，默认为当前时间
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE -- 外键，关联用户表的ID，用户删除时级联删除
);

-- 创建收入表
CREATE TABLE `income` (
    `id` INT AUTO_INCREMENT PRIMARY KEY, -- 收入表的主键，自增
    `user_id` INT NOT NULL, -- 关联的用户ID，不能为空
    `account_id` INT NOT NULL, -- 关联的账户ID，不能为空
    `category` VARCHAR(50) NOT NULL, -- 收入分类，最大长度50，不能为空
    `amount` DECIMAL(10, 2) NOT NULL, -- 收入金额，不能为空
    `description` TEXT, -- 收入描述，可为空
    `date` DATE NOT NULL, -- 收入日期，不能为空
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE, -- 外键，关联用户表的ID，用户删除时级联删除
    FOREIGN KEY (`account_id`) REFERENCES `account`(`id`) ON DELETE CASCADE -- 外键，关联账户表的ID，账户删除时级联删除
);

-- 创建支出表
CREATE TABLE `expense` (
    `id` INT AUTO_INCREMENT PRIMARY KEY, -- 支出表的主键，自增
    `user_id` INT NOT NULL, -- 关联的用户ID，不能为空
    `account_id` INT NOT NULL, -- 关联的账户ID，不能为空
    `category` VARCHAR(50) NOT NULL, -- 支出分类，最大长度50，不能为空
    `amount` DECIMAL(10, 2) NOT NULL, -- 支出金额，不能为空
    `description` TEXT, -- 支出描述，可为空
    `date` DATE NOT NULL, -- 支出日期，不能为空
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE, -- 外键，关联用户表的ID，用户删除时级联删除
    FOREIGN KEY (`account_id`) REFERENCES `account`(`id`) ON DELETE CASCADE -- 外键，关联账户表的ID，账户删除时级联删除
);

-- 创建预算表
CREATE TABLE `budget` (
    `id` INT AUTO_INCREMENT PRIMARY KEY, -- 预算表的主键，自增
    `user_id` INT NOT NULL, -- 关联的用户ID，不能为空
    `category` VARCHAR(50) NOT NULL, -- 预算分类，最大长度50，不能为空
    `amount` DECIMAL(10, 2) NOT NULL, -- 预算金额，不能为空
    `start_date` DATE NOT NULL, -- 预算开始日期，不能为空
    `end_date` DATE NOT NULL, -- 预算结束日期，不能为空
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE -- 外键，关联用户表的ID，用户删除时级联删除
);

-- 创建账单表
CREATE TABLE `bill` (
    `id` INT AUTO_INCREMENT PRIMARY KEY, -- 账单表的主键，自增
    `user_id` INT NOT NULL, -- 关联的用户ID，不能为空
    `account_id` INT NOT NULL, -- 关联的账户ID，不能为空
    `amount` DECIMAL(10, 2) NOT NULL, -- 账单金额，不能为空
    `due_date` DATE NOT NULL, -- 账单到期日期，不能为空
    `status` ENUM('未支付', '已支付') DEFAULT '未支付', -- 账单支付状态，默认为未支付
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE, -- 外键，关联用户表的ID，用户删除时级联删除
    FOREIGN KEY (`account_id`) REFERENCES `account`(`id`) ON DELETE CASCADE -- 外键，关联账户表的ID，账户删除时级联删除
);

-- 创建备份表
CREATE TABLE `backup` (
    `id` INT AUTO_INCREMENT PRIMARY KEY, -- 备份表的主键，自增
    `user_id` INT NOT NULL, -- 关联的用户ID，不能为空
    `file_path` VARCHAR(255) NOT NULL, -- 备份文件路径，最大长度255，不能为空
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 备份创建时间，默认为当前时间
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE -- 外键，关联用户表的ID，用户删除时级联删除
);