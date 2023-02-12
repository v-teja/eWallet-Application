package com.project.majorproject;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet,Integer> {
    Wallet getWalletByUserName(String fromUser);
}
