package com.tjut.bean;

import com.tjut.enums.KeyBorad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Move {
    private KeyBorad keyBorad;

    private long timestamp;

}
