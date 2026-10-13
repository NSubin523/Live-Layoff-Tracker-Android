package com.example.tracklayoff.core.common.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*

class UserSessionTest {
    @Test fun anonymousFirebaseSessionIsStillAGuest() = runTest {
        val auth = mock(FirebaseAuth::class.java)
        val user = mock(FirebaseUser::class.java)
        `when`(auth.currentUser).thenReturn(user)
        `when`(user.uid).thenReturn("anonymous")
        `when`(user.isAnonymous).thenReturn(true)
        doAnswer { call -> call.getArgument<FirebaseAuth.AuthStateListener>(0).onAuthStateChanged(auth); null }
            .`when`(auth).addAuthStateListener(any(FirebaseAuth.AuthStateListener::class.java))
        val repository = UserRepository(auth)
        assertNull(repository.currentUserId())
        assertNull(repository.userIds.first())
        `when`(user.isAnonymous).thenReturn(false)
        assertEquals("anonymous", repository.currentUserId())
        assertEquals("anonymous", repository.userIds.first())
        verify(auth, times(2)).removeAuthStateListener(any(FirebaseAuth.AuthStateListener::class.java))
    }
}
