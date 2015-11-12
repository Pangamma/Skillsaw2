package com.lumengaming.skillsaw.service;

import java.util.ArrayList;

public interface AsyncManyCallback<T>{
	public void doCallback(ArrayList<T> t);
}
