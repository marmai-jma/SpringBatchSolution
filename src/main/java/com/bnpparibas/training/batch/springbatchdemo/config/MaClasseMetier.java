package com.bnpparibas.training.batch.springbatchdemo.config;

import org.springframework.stereotype.Component;

import com.bnpparibas.training.batch.springbatchdemo.dto.BookDto;

@Component
public class MaClasseMetier {

	public BookDto maMethodeMetier(final BookDto bookDto) {
		if (bookDto.getPublishedOn() > 2019) {
			return null;
		}
		return bookDto;
	}

}
