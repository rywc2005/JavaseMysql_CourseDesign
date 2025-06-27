package com.PFM.CD.gui.controller;

import com.PFM.CD.gui.panel.AccountsPanel;
import com.PFM.CD.service.dto.AccountDto;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.AccountService;


import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-27
 * @Description:
 * @Version: 17.0
 */


public class AccountControllerImpl implements AccountController {
    private final AccountService accountService;

    public AccountControllerImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public List<AccountDto> queryAccounts(int userId, String keyword) {
        try {
            List<AccountDto> all = accountService.getUserAccounts(userId)
                    .stream()
                    .map(acc -> new AccountDto(
                            acc.getAccountId(),
                            acc.getUserId(),
                            acc.getAccountName(),
                            acc.getBalance(),
                            acc.getStatus()
                    ))
                    .toList();
            if (keyword == null || keyword.isEmpty()) return all;
            return all.stream().filter(a ->
                    (a.getAccountName() != null && a.getAccountName().contains(keyword))
            ).toList();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "加载账户失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return java.util.Collections.emptyList();
        }
    }

    @Override
    public void addAccount(int userId, String accountName, BigDecimal initialBalance) throws ServiceException {
        accountService.createAccount(userId, accountName, initialBalance);
    }

    @Override
    public void updateAccount(AccountDto account) throws ServiceException {
        com.PFM.CD.entity.Account entity = accountService.getAccountById(account.getAccountId());
        entity.setAccountName(account.getAccountName());
        accountService.updateAccount(entity);
    }

    @Override
    public void deleteAccount(int accountId, Integer transferAccountId) throws ServiceException {
        accountService.deleteAccount(accountId, transferAccountId);
    }

    @Override
    public AccountDto getAccountById(int accountId) {
        try {
            com.PFM.CD.entity.Account acc = accountService.getAccountById(accountId);
            return acc == null ? null : new AccountDto(
                    acc.getAccountId(),
                    acc.getUserId(),
                    acc.getAccountName(),
                    acc.getBalance(),
                    acc.getStatus()
            );
        } catch (ServiceException e) {
            return null;
        }
    }
}