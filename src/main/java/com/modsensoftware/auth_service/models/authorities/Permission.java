package com.modsensoftware.auth_service.models.authorities;

public enum Permission {
    //library_service permissions
    BREATHE,
    REGISTER_LIBRARY_SERVICE_USER,
    BORROW_BOOK_ON_USER,
    RETURN_BOOK_FOR_USER,
    ADD_BOOKS_TO_LIBRARY,
    GET_ALL_LIBRARY_BOOKS,
    GET_LOANS_BY_USER,

    //book_service permissions
    GET_ALL_BOOKS,
    GET_BOOK_BY_ID,
    GET_BOOK_BY_ISBN,
    ADD_BOOK,
    EDIT_BOOK,
    DELETE_BOOK_BY_ID,
    DELETE_BOOK_BY_ISBN,
}

