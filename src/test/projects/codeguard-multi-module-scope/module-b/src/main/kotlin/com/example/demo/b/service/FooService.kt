package com.example.demo.b.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FooService {
    @Transactional
    fun outer() {
        this.inner()
    }

    @Transactional
    fun inner() {
        // Intentionally empty.
    }
}
