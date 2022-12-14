package com.practice.overviewspringsecurity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
  private int statusCode;
  private Date timeStamp;
  private String message;
  private String description;
}
