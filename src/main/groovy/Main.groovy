class ReconstructorBinaryImage {
    int size
    int[][] targetImage
    int[][] randomTarget
    Random random = new Random()

    ReconstructorBinaryImage(int size, int[][] targetImage) {
        this.size = size
        this.targetImage = targetImage
        this.randomTarget = generateRandomImage()
    }

    int calculateDiscrepancy(int[][] image, int[][] reference) {
        return (0..<size).sum { i ->
            (0..<size).count { j -> image[i][j] != reference[i][j] }
        } as int
    }

    // random image
    int[][] generateRandomImage() {
        return (0..<size).collect {
            (0..<size).collect { random.nextInt(2) } as int[]
        } as int[][]
    }

    // generate neighbor
    int[][] generateNeighbor(int[][] currentImage) {
        def neighbor = currentImage*.clone() as int[][]
        def x = random.nextInt(size)
        def y = random.nextInt(size)
        neighbor[x][y] = neighbor[x][y] ? 0 : 1
        return neighbor
    }

    // Visualize the image
    void visualizeImage(int[][] image, String title, int[][] reference) {
        println("\n${title} (Discrepancy: ${calculateDiscrepancy(image, reference)})")
        image.each { row ->
            println(row.collect { it == 1 ? '█' : '░' }.join())
        }
    }

    def hillClimbing(int[][] initialImage, int[][] target, String phaseName, int maxIterations = 500) {
        int[][] currentImage = initialImage
        int currentScore = calculateDiscrepancy(currentImage, target)

        visualizeImage(currentImage, "Initial image ($phaseName)", target)
        visualizeImage(target, "Target image ($phaseName)", target)
        if (currentScore == 0) {
            return currentImage
        }

        (0..<maxIterations).each { i ->
            int[][] neighbor = generateNeighbor(currentImage)
            int neighborScore = calculateDiscrepancy(neighbor, target)

            if (neighborScore < currentScore) {
                currentImage = neighbor
                currentScore = neighborScore
                if (i % 100 == 0) { // Visualize every 100 iterations
                    visualizeImage(currentImage, "Progress ($phaseName iteration ${i + 1})", target)
                }
            }
        }

        visualizeImage(currentImage, "Final result ($phaseName)", target)
        return currentImage
    }

    void runBothPhases() {
        println("PHASE 1: Random to Smiley")
        int[][] perfectSmiley = hillClimbing(generateRandomImage(), targetImage, "Random to Smiley")

        println("\nPHASE 2: Smiley to Random")
        this.randomTarget = generateRandomImage() // get random image again
        hillClimbing(perfectSmiley, randomTarget, "Smiley to Random")
    }
}


static void main(String[] args) {
    def size = 10
    def smileyFace = [
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 1, 1, 1, 1, 1, 1, 1, 1, 0],
            [0, 1, 0, 0, 0, 0, 0, 0, 1, 0],
            [0, 1, 0, 1, 0, 0, 1, 0, 1, 0],
            [0, 1, 0, 0, 0, 0, 0, 0, 1, 0],
            [0, 1, 0, 1, 0, 0, 1, 0, 1, 0],
            [0, 1, 0, 1, 1, 1, 1, 0, 1, 0],
            [0, 1, 0, 0, 0, 0, 0, 0, 1, 0],
            [0, 1, 1, 1, 1, 1, 1, 1, 1, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    ] as int[][]

    def reconstructor = new ReconstructorBinaryImage(size, smileyFace)
    reconstructor.runBothPhases()
}