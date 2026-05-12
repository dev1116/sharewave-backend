package com.demo.sharewave.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.sharewave.entity.Transfer;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Transfer findByRoomId(String roomId);
    List<Transfer> findByReceiverEmail(String receiverEmail);
}