const double bias0 = 1.0;
const double bias1 = 1.0;
const int layer0 = 4;
const int layer1 = 4;
const int layer2 = 5;

double **weights1;
double **weights2;

inline double sigmoid(double x) {
	return (1.0 / (1 + exp(-x) ));
}

double* product(double* input, double** weights, int l, int c, double bias) {
	double* output = new double[c + 1]; // +1 for bias input (optimization)
	for(int j = 0; j < c; j++)  {
		output[j] = 0;
		for(int i = 0; i < l; i++) {
			output[j] += input[i] * weights[i][j];
		}
		output[j] = sigmoid(output[j]);
	}
	output[c] = bias;
	return output;
}

double* computeOutput(double* input, int size) {
	input[size] = bias0;
	double *output1 = product(input, weights1, layer0, layer1, bias1);
	return product(output1, weights2, layer1, layer2, 0);
}
