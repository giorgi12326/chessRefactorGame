package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class SquareDto implements Serializable {
    int x;
    int y;
}
