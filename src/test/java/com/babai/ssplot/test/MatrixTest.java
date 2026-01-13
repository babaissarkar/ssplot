package com.babai.ssplot.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.babai.ssplot.math.prim.Matrix;

class MatrixTest {

	@Test
	void constructorRejectsJaggedArray() {
		double[][] bad = {
				{1, 2},
				{3}
		};
		assertThrows(IllegalArgumentException.class, () -> new Matrix(bad));
	}

	@Test
	void constructorCopiesDataDefensively() {
		double[][] src = {
				{1, 2},
				{3, 4}
		};
		Matrix m = new Matrix(src);
		src[0][0] = 99;
		assertEquals(1.0, m.get(0, 0));
	}

	@Test
	void testAdd() {
		Matrix a = new Matrix(new double[][]{
			{1, 2},
			{3, 4}
		});
		Matrix b = new Matrix(new double[][]{
			{5, 6},
			{7, 8}
		});

		Matrix c = a.add(b);

		assertEquals(6, c.get(0, 0));
		assertEquals(8,  c.get(0, 1));
		assertEquals(10, c.get(1, 0));
		assertEquals(12, c.get(1, 1));
	}

	@Test
	void addRejectsDimensionMismatch() {
		Matrix a = new Matrix(new double[][]{{1, 2}});
		Matrix b = new Matrix(new double[][]{{1}, {2}});
		assertThrows(IllegalArgumentException.class, () -> a.add(b));
	}

	@Test
	void testScalarMultiply() {
		Matrix m = new Matrix(new double[][]{
			{1, -2},
			{3, 0}
		});

		Matrix r = m.times(2.0);

		assertEquals(2, r.get(0, 0));
		assertEquals(-4, r.get(0, 1));
		assertEquals(6, r.get(1, 0));
	}

	@Test
	void testMatrixMultiply() {
		Matrix a = new Matrix(new double[][]{
			{1, 2, 3},
			{4, 5, 6}
		});

		Matrix b = new Matrix(new double[][]{
			{7, 8},
			{9, 10},
			{11, 12}
		});

		Matrix c = a.multiply(b);

		assertEquals(58, c.get(0, 0));
		assertEquals(64, c.get(0, 1));
		assertEquals(139, c.get(1, 0));
		assertEquals(154, c.get(1, 1));
	}

	@Test
	void multiplyRejectsBadDimensions() {
		Matrix a = new Matrix(new double[][]{{1, 2}});
		Matrix b = new Matrix(new double[][]{{1, 2}});
		assertThrows(IllegalArgumentException.class, () -> a.multiply(b));
	}

	@Test
	void testTransposeNonSquare() {
		Matrix m = new Matrix(new double[][]{
			{1, 2, 3},
			{4, 5, 6}
		});

		Matrix t = m.transpose();

		assertEquals(1, t.get(0, 0));
		assertEquals(4, t.get(0, 1));
		assertEquals(2, t.get(1, 0));
		assertEquals(6, t.get(2, 1));
	}

	@Test
	void setMutatesOnlyTargetCell() {
		Matrix m = new Matrix(new double[][]{
			{1, 2},
			{3, 4}
		});

		m.set(99, 0, 1);

		assertEquals(99, m.get(0, 1));
		assertEquals(1, m.get(0, 0));
		assertEquals(4, m.get(1, 1));
	}

	@Test
	void operationsDoNotMutateOriginalMatrix() {
		Matrix a = new Matrix(new double[][]{
			{1, 2},
			{3, 4}
		});
		Matrix b = new Matrix(new double[][]{
			{5, 6},
			{7, 8}
		});

		a.add(b);
		a.times(2);
		a.transpose();

		assertEquals(1, a.get(0, 0));
		assertEquals(4, a.get(1, 1));
	}
}

