package com.example.byteduo


import com.example.byteduo.Model.CartItem
import com.example.byteduo.Model.CustomerReview
import com.example.byteduo.Model.MenuItems
import com.example.byteduo.Model.Order
import com.example.byteduo.View.OrdersAdapter.ReviewsAdapter
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitTesting {

    @Test
    fun onBindViewHolder_shouldBindDataCorrectly() {
        val reviews = listOf(
            CustomerReview("Customer1", "Order1", "Richard",4f, "Good service"),
            CustomerReview("Customer2", "Order2", "Kankam",3f, "Average experience"))

        val adapter = ReviewsAdapter(reviews)
        assertEquals(reviews.size, adapter.itemCount)
    }
    private lateinit var menuItems: MenuItems

    @Test
    fun testOrderCreation() {
        // Create an instance of Order
        val order = Order(
            orderId = "123",
            cusId = "456",
            orderTime = "2022-01-01T12:00:00",
            orderStatus = "Pending",
            orderItems = listOf(
                CartItem(userId = "Richard", menuItem = MenuItems("Latte"), quantity = 2),
                CartItem(userId = "Mark", menuItem = MenuItems("Drink"), quantity = 1)
            ),
            paymentType = "Credit Card"
        )

        // Test the properties of the order
        assertEquals("123", order.orderId)
        assertEquals("456", order.cusId)
        assertEquals("2022-01-01T12:00:00", order.orderTime)
        assertEquals("Pending", order.orderStatus)
        assertEquals("Credit Card", order.paymentType)

        // Test the orderItems property
        assertEquals(2, order.orderItems?.size)
        assertEquals("Richard", order.orderItems?.get(0)?.userId)
        assertEquals(1, order.orderItems?.get(1)?.quantity)
    }


}