package me.roryclaasen.cavegen;

import java.util.Random;

import me.roryclaasen.cavegen.noise.OpenSimplexNoise;

public class CaveGenerator {

	private final int WIDTH, HEIGHT;
	private final long SEED;

	private OpenSimplexNoise noise;

	private int[] tiles;

	public CaveGenerator(int width, int height) {
		this(width, height, new Random().nextLong());
	}

	public CaveGenerator(int width, int height, long seed) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.SEED = seed;

		this.tiles = new int[WIDTH * HEIGHT];
	}

	public void generate() {
		generate(new CaveConfig());
	}

	public void generate(CaveConfig config) {
		Random random = new Random(SEED);
		if (config.doDebugOutput()) System.out.println("Creating level with seed: " + this.SEED);
		noise = new OpenSimplexNoise(this.SEED);
		if (config.doDebugOutput()) System.out.println("Setting tiles");
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				double value = noise.eval(x / config.getFeatureSize(), y / config.getFeatureSize());
				if (value > -config.getCaveRange() && value < config.getCaveRange()) tiles[x + y * WIDTH] = 1;
			}
		}
		for (int i = 0; i < 10; i++) {
			boolean valid = false;
			int x = 0, y = 0, size = 18;
			while (!valid) {
				x = random.nextInt(WIDTH);
				y = random.nextInt(HEIGHT);
				valid = tiles[x + y * WIDTH] == 1;
			}
			// for (int l = 0; l < 3; l++) {
			// placeCave(x - (size / 2) + random.nextInt(size), y - (size / 2) + random.nextInt(size), size / (1 + random.nextInt(3)) + random.nextInt(size));
			// }
			placeCave(x, y, (int) Math.ceil((size * 0.75) + random.nextInt(size)));
		}
		if (config.doDebugOutput()) System.out.println("Done!");
	}

	private void placeCave(int sx, int sy, int size) {
		for (int x = sx - (size / 2); x < sx + (size / 2); x++) {
			if (x < 0 || x >= WIDTH) continue;
			for (int y = sy - (size / 2); y < sy + (size / 2); y++) {
				if (y < 0 || y >= HEIGHT) continue;
				int xl = Math.abs(sx - x);
				int yl = Math.abs(sy - y);
				if (Math.round(Math.sqrt((xl * xl) + (yl * yl))) < size / 2) tiles[x + y * WIDTH] = 1;
			}
		}
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public int getTile(int x, int y) {
		if (x < 0 || x >= WIDTH) throw new IndexOutOfBoundsException("Index " + x + " is out of bounds!");
		if (y < 0 || y >= HEIGHT) throw new IndexOutOfBoundsException("Index " + y + " is out of bounds!");
		return tiles[x + y * WIDTH];
	}

	public long getSeed() {
		return SEED;
	}
}
