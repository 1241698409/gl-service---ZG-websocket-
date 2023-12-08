package com.baosight.gl.service.ht.thread;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.baosight.gl.service.ht.domain.Point;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("all")
@Slf4j
public class CountUTask implements Runnable {

	public CountDownLatch countDownLatch;
	public int startIndex;
	public int endIndex;
	public int width;
	public int height;
	public double xStart;
	public double yStart;
	public double zStart;
	public double stepX;
	public double stepY;
	public double stepZ;
	public double radius;
	public Point point;
	public List<Double> rResult;

	public CountUTask() {

	}

	@Override
	public void run() {
		// 声明x、y、z
		double x;
		double y;
		double z;
		// 循环遍历tall、height、width
		for (int t = startIndex; t < endIndex; t++) {
			/*log.error(Thread.currentThread().getName() + ":" + startIndex + ":" + endIndex + ":" + t);*/
			y = yStart + stepY * t;
			for (int h = 0; h < height; h++) {
				z = zStart + stepZ * h;
				for (int w = 0; w < width; w++) {
					x = xStart + stepX * w;
					double[] value = point.getZValue(x, y, z, radius, 7, "u");
					for (int j = 0; j < value.length; j++) {
						rResult.add(value[j]);
					}
				}
			}
		}
		countDownLatch.countDown();
	}
}