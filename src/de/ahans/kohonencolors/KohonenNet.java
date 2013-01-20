package de.ahans.kohonencolors;

public class KohonenNet {
	float epsilon_i = 0.1f, epsilon_f = 0.015f, sigma_i = 5.0f, sigma_f = 0.2f,
			err = 0f;
	int t_max = 40000, t, gridN, gridM, dim, bmnI = 0, bmnJ = 0;
	float x[];

	public float weights[][][];

	public KohonenNet(int gridWidth, int gridHeight, int inputDim) {
		gridN = gridWidth;
		gridM = gridHeight;
		dim = inputDim;
		t = 0;
		x = new float[dim];
		initWeights();
	}

	public KohonenNet(int gridWidth, int gridHeight, int inputDim, float e_i,
			float e_f, float s_i, float s_f, int tm) {
		gridN = gridWidth;
		gridM = gridHeight;
		dim = inputDim;
		epsilon_i = e_i;
		epsilon_f = e_f;
		sigma_i = s_i;
		sigma_f = s_f;
		t_max = tm;
		t = 0;
		x = new float[dim];
		initWeights();
	}

	/**
	 * Lerne steps Schritte mit zuf�lligem Input
	 */
	public void learn(int steps) {
		for (int i = 0; i < steps; i++) {
			// random input
			for (int k = 0; k < dim; k++)
				x[k] = (float) Math.random();
			// learn step
			learn(x);
		}
	}

	/**
	 * Lerne einen Schritt mit gegebenen Input
	 */
	public void learn(float x[]) {
		if (t < t_max) {
			this.x = x;
			learnStep(x);
		}
	}

	/**
	 * Ermittelt Best Matching Neuron des letzten Lernschrittes
	 */
	public int[] getLastBestMatching() {
		int bmn[] = new int[2];
		bmn[0] = bmnI;
		bmn[1] = bmnJ;
		return bmn;
	}

	/**
	 * Liefert Input des letzten Lernschrittes
	 */
	public float[] getLastInput() {
		return x;
	}

	/**
	 * Initialisiert alle Gewichte neu
	 */
	public void reset() {
		t = 0;
		initWeights();
	}

	/**
	 * Ermittelt die bereits ausgef�hrten Lernschritte
	 */
	public int getSteps() {
		return t;
	}

	public float getLastError() {
		return err;
	}

	/**
	 * Setzt sigma_i auf einen neuen Wert
	 */
	public void setSigma_i(float sigma_i) {
		this.sigma_i = sigma_i;
	}

	public void setEpsilon_i(float epsilon_i) {
		this.epsilon_i = epsilon_i;
	}

	public void setT_max(int t_max) {
		this.t_max = t_max;
	}

	void learnStep(float x[]) {
		float epsilon, sigma, dst, minDst = 999999;
		;
		int i, j, k;

		++t;
		epsilon = (float) (epsilon_i * Math.pow(epsilon_f / epsilon_i, t
				/ t_max));
		sigma = (float) (sigma_i * Math.pow(sigma_f, t / t_max));

		// Best Matching Neuron ermitteln
		bmnI = 0;
		bmnJ = 0;
		for (i = 0; i < gridN; i++)
			for (j = 0; j < gridM; j++) {
				dst = euklidDst(x, weights[i][j]);
				if (dst < minDst) {
					minDst = dst;
					bmnI = i;
					bmnJ = j;
				}
			}

		err = minDst;

		// alle Gewichte anpassen
		for (i = 0; i < gridN; i++)
			for (j = 0; j < gridM; j++) {
				int gridDst = Math.abs(bmnI - i) + Math.abs(bmnJ - j);
				float gain = (float) (Math.exp(-(gridDst * gridDst)
						/ (2.0 * sigma * sigma)) * epsilon);

				for (k = 0; k < dim; k++)
					weights[i][j][k] += gain * (x[k] - weights[i][j][k]);
			}
	}

	void initWeights() {
		int i, j, k;
		// Gewichte anlegen und initialisieren
		weights = new float[gridN][gridM][dim];
		for (i = 0; i < gridN; i++)
			for (j = 0; j < gridM; j++)
				for (k = 0; k < dim; k++)
					weights[i][j][k] = (float) Math.random();
	}

	float euklidDst(float x[], float w[]) {
		float d = 0;
		for (int i = 0; i < dim; i++)
			d += (x[i] - w[i]) * (x[i] - w[i]);
		return d;
	}
}
