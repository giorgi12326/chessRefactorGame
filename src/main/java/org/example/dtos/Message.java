package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class Message implements Serializable {
    public String type;
    public Object payload;
}
