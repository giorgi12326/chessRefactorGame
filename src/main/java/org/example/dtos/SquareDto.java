package org.example.dtos;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class SquareDto implements Serializable {
    int number;
    char letter;
    char piece;
}
