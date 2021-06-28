package math.prim;

/* Matrix with generalized elements.*/
/* Focusing on On-demand calculations.*/
public class GenMatrix<T extends MathObject<T>> {
    public int rows, columns; /* Number of rows and columns. */
    public T[][] elems;

    public GenMatrix(T[][] data, int rows, int cols) {
        this.rows = rows;
        this.columns = cols;
        this.elems = data;
    }

    public GenMatrix<T> add(GenMatrix<T> m) {
        T[][] mdata = m.toArray();
		T[][] res = this.elems; /*TODO = GenMatrix<T>.nullMatrix() */
        if ((this.rows == m.getRows()) && (this.columns == m.getColumns())) {
            for(int i = 0; i < m.getRows(); i++) {
                for(int j = 0; j < m.getColumns(); j++) {
                    res[i][j] = elems[i][j].add(mdata[i][j]);
                }
            }
        } else {
            /* Return some value of res? */
            /* At least throw an Exception? */
        }

        return new GenMatrix<T>(res, this.rows, this.columns);
    }

    public GenMatrix<T> subtract(GenMatrix<T> m) {
        T[][] mdata = m.toArray();
		T[][] res = this.elems; /*TODO = GenMatrix<T>.nullMatrix() */
        if ((this.rows == m.getRows()) && (this.columns == m.getColumns())) {
            for(int i = 0; i < m.getRows(); i++) {
                for(int j = 0; j < m.getColumns(); j++) {
                    res[i][j] = elems[i][j].subtract(mdata[i][j]);
                }
            }
        } else {
            /* Return some value of res? */
            /* At least throw an Exception? */
        }

        return new GenMatrix<T>(res, this.rows, this.columns);
    }

    public GenMatrix<T> multiply(GenMatrix<T> m) {
        return m;
    }


    /**
	 * @return array created from this Matrix
	 */
	public T[][] toArray() {
		return elems;
	}

    /**
	 * @return number of rows
	 */
	private double getRows() {
		return this.rows;
	}
	
	/**
	 * @return number of columns
	 */
	private double getColumns() {
		return this.columns;
	}

    @Override
	public String toString() {
		StringBuffer mat = new StringBuffer("");
		for(int i = 0; i < rows; i++) {
			mat.append("[");
			for(int j = 0; j < columns; j++) {
				mat.append(elems[i][j]);
				if (j != columns - 1) {
					mat.append(", ");
				}
			}
			mat.append("]");
			if (i != rows-1) {
				mat.append("\n");
			}
		}
		return mat.toString();
	}

}
