package com.PFM.CD.gui.controller;

import com.PFM.CD.service.dto.AccountDto;
import com.PFM.CD.service.exception.ServiceException;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-27
 * @Description:
 * @Version: 17.0
 */


public interface AccountController {
    List<AccountDto> queryAccounts(int userId, String keyword);
    void addAccount(int userId, String accountName, BigDecimal initialBalance) throws ServiceException;
    void updateAccount(AccountDto account) throws ServiceException;
    void deleteAccount(int accountId, Integer transferAccountId) throws ServiceException;
    AccountDto getAccountById(int accountId);
}
