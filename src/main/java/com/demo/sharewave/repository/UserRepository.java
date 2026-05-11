package com.demo.sharewave.repository;

import org.springframework.stereotype.Repository;

import com.demo.sharewave.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {

	
	User findByEmail(String email);
}
