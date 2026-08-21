package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.CarromRepository
import com.example.data.UserStatsEntity
import com.example.model.AiDifficulty
import com.example.model.AimTrajectory
import com.example.model.BoardTheme
import com.example.model.CarromPhysics
import com.example.model.CarromPiece
import com.example.model.GameMode
import com.example.model.Particle
import com.example.model.PieceType
import com.example.model.StrikerTheme
import com.example.model.TrickShotLevel
import com.example.model.Vector2D
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class GameUiState(
    val gameMode: GameMode = GameMode.CLASSIC_POINTS,
    val boardTheme: BoardTheme = BoardTheme.ClassicMahogany,
    val strikerTheme: StrikerTheme = StrikerTheme.ClassicIvory,
    val pieces: List<CarromPiece> = emptyList(),
    val striker: CarromPiece? = null,
    val strikerSliderFraction: Float = 0.5f,
    val strikerAimVector: Vector2D = Vector2D(0f, -1f),
    val shotPowerPercent: Float = 50f,
    val isAiming: Boolean = false,
    val isPhysicsRunning: Boolean = false,
    val currentPlayer: Int = 1, // 1 or 2
    val player1Name: String = "Player 1",
    val player2Name: String = "Player 2",
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val player1PocketedWhite: Int = 0,
    val player1PocketedBlack: Int = 0,
    val player2PocketedWhite: Int = 0,
    val player2PocketedBlack: Int = 0,
    val player1AssignedColor: PieceType = PieceType.WHITE,
    val player2AssignedColor: PieceType = PieceType.BLACK,
    val queenPocketedBy: Int? = null, // which player pocketed queen
    val isQueenCoverRequired: Boolean = false,
    val isGameOver: Boolean = false,
    val winnerName: String? = null,
    val alertMessage: String? = null,
    val lastShotStats: String? = null,
    val aiDifficulty: AiDifficulty = AiDifficulty.MEDIUM,
    val isAiThinking: Boolean = false,
    val trickShotLevel: TrickShotLevel? = null,
    val trickShotShotsTaken: Int = 0,
    val trickShotTargetCount: Int = 0,
    val trickShotStarsEarned: Int = 0,
    val particles: List<Particle> = emptyList(),
    val isPaused: Boolean = false
)

class CarromGameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CarromRepository.getInstance(application)
    private val soundManager = SoundManager.getInstance(application)

    val userStats: StateFlow<UserStatsEntity?> = repository.userStats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val recentMatches = repository.recentMatches.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var physicsJob: Job? = null
    private var aiJob: Job? = null

    // Track pieces pocketed during the current single shot
    private val currentShotPocketed = mutableListOf<CarromPiece>()
    private var strikerPocketedThisTurn = false
    private var queenPocketedThisTurn = false

    init {
        viewModelScope.launch {
            val stats = repository.getOrCreateStats()
            soundManager.soundEnabled = stats.soundEnabled
            soundManager.hapticsEnabled = stats.hapticsEnabled
            val board = BoardTheme.ALL_BOARDS.find { it.id == stats.selectedBoardId } ?: BoardTheme.ClassicMahogany
            val striker = StrikerTheme.ALL_STRIKERS.find { it.id == stats.selectedStrikerId } ?: StrikerTheme.ClassicIvory
            _uiState.update { it.copy(boardTheme = board, strikerTheme = striker) }
        }
    }

    fun startNewGame(
        mode: GameMode,
        p1Name: String = "Player 1",
        p2Name: String = if (mode == GameMode.VS_AI) "Carrom Bot" else "Player 2",
        difficulty: AiDifficulty = AiDifficulty.MEDIUM,
        trickLevel: TrickShotLevel? = null
    ) {
        physicsJob?.cancel()
        aiJob?.cancel()
        currentShotPocketed.clear()
        strikerPocketedThisTurn = false
        queenPocketedThisTurn = false

        val stats = userStats.value
        val board = BoardTheme.ALL_BOARDS.find { it.id == (stats?.selectedBoardId ?: "classic_mahogany") }
            ?: BoardTheme.ClassicMahogany
        val strikerSkin = StrikerTheme.ALL_STRIKERS.find { it.id == (stats?.selectedStrikerId ?: "classic_ivory") }
            ?: StrikerTheme.ClassicIvory

        val pieces = if (mode == GameMode.TRICK_SHOTS && trickLevel != null) {
            trickLevel.initialPieces.map { it.copy() }
        } else {
            CarromPhysics.createStandardPieces()
        }

        val initialStriker = createStrikerForPlayer(1, strikerSkin, 0.5f)

        _uiState.value = GameUiState(
            gameMode = mode,
            boardTheme = board,
            strikerTheme = strikerSkin,
            pieces = pieces,
            striker = initialStriker,
            strikerSliderFraction = 0.5f,
            strikerAimVector = Vector2D(0f, -1f),
            shotPowerPercent = 50f,
            isAiming = false,
            isPhysicsRunning = false,
            currentPlayer = 1,
            player1Name = p1Name,
            player2Name = p2Name,
            player1Score = 0,
            player2Score = 0,
            player1PocketedWhite = 0,
            player1PocketedBlack = 0,
            player2PocketedWhite = 0,
            player2PocketedBlack = 0,
            player1AssignedColor = PieceType.WHITE,
            player2AssignedColor = PieceType.BLACK,
            queenPocketedBy = null,
            isQueenCoverRequired = false,
            isGameOver = false,
            winnerName = null,
            alertMessage = "Match Started! Turn: $p1Name",
            aiDifficulty = difficulty,
            isAiThinking = false,
            trickShotLevel = trickLevel,
            trickShotShotsTaken = 0,
            trickShotTargetCount = 0,
            trickShotStarsEarned = 0,
            particles = emptyList(),
            isPaused = false
        )
    }

    private fun createStrikerForPlayer(player: Int, theme: StrikerTheme, sliderFraction: Float): CarromPiece {
        val minX = CarromPhysics.BASELINE_MIN_X
        val maxX = CarromPhysics.BASELINE_MAX_X
        val posX = minX + (maxX - minX) * sliderFraction.coerceIn(0f, 1f)
        val posY = if (player == 1) CarromPhysics.BASELINE_BOTTOM_Y else CarromPhysics.BASELINE_TOP_Y

        return CarromPiece(
            id = 9999,
            type = PieceType.STRIKER,
            position = Vector2D(posX, posY),
            velocity = Vector2D(0f, 0f),
            radius = CarromPhysics.STRIKER_RADIUS,
            mass = theme.mass
        )
    }

    fun onStrikerSliderChange(fraction: Float) {
        if (_uiState.value.isPhysicsRunning || _uiState.value.isGameOver || _uiState.value.isAiThinking) return
        val current = _uiState.value
        val newStriker = createStrikerForPlayer(current.currentPlayer, current.strikerTheme, fraction)
        _uiState.update {
            it.copy(
                strikerSliderFraction = fraction,
                striker = newStriker
            )
        }
    }

    fun onAimVectorChange(aimVector: Vector2D, powerPercent: Float) {
        if (_uiState.value.isPhysicsRunning || _uiState.value.isGameOver || _uiState.value.isAiThinking) return
        _uiState.update {
            it.copy(
                strikerAimVector = aimVector,
                shotPowerPercent = powerPercent.coerceIn(5f, 100f),
                isAiming = true
            )
        }
    }

    fun onAimRelease() {
        val state = _uiState.value
        if (state.isPhysicsRunning || state.isGameOver || state.isAiThinking || state.striker == null) return

        val power = state.shotPowerPercent
        val aimDir = state.strikerAimVector.normalized()
        val powerMultiplier = state.strikerTheme.powerMultiplier
        val initialVelocity = aimDir * (power * 24f * powerMultiplier)

        soundManager.playStrikerHit(power)

        val launchedStriker = state.striker.copy(velocity = initialVelocity)
        _uiState.update {
            it.copy(
                striker = launchedStriker,
                isAiming = false,
                isPhysicsRunning = true,
                alertMessage = null
            )
        }

        currentShotPocketed.clear()
        strikerPocketedThisTurn = false
        queenPocketedThisTurn = false

        if (state.gameMode == GameMode.TRICK_SHOTS) {
            _uiState.update { it.copy(trickShotShotsTaken = it.trickShotShotsTaken + 1) }
        }

        startPhysicsLoop()
    }

    private fun startPhysicsLoop() {
        physicsJob?.cancel()
        physicsJob = viewModelScope.launch {
            val dt = 0.016f
            val frictionMultiplier = _uiState.value.boardTheme.frictionMultiplier

            while (isActive) {
                val state = _uiState.value
                val striker = state.striker
                val activeList = if (striker != null && !striker.isPocketed) {
                    state.pieces + striker
                } else {
                    state.pieces
                }

                // Step physics with particle spawns and sound callbacks
                val updated = CarromPhysics.stepPhysics(
                    pieces = activeList,
                    dt = dt,
                    subSteps = 8,
                    frictionBase = 1.15f * frictionMultiplier,
                    cushionRestitution = 0.88f,
                    pieceRestitution = 0.94f,
                    onPieceHit = { intensity -> soundManager.playDiscHit(intensity) },
                    onCushionHit = { intensity -> soundManager.playCushionBounce(intensity) },
                    onPocketSink = { piece ->
                        soundManager.playPocketSink()
                        currentShotPocketed.add(piece)
                        if (piece.type == PieceType.STRIKER) {
                            strikerPocketedThisTurn = true
                        }
                        if (piece.type == PieceType.QUEEN) {
                            queenPocketedThisTurn = true
                        }
                        spawnPocketParticles(piece.position)
                    }
                )

                // Update particles
                val newParticles = updateParticles(state.particles, dt)

                val newStriker = updated.find { it.type == PieceType.STRIKER }
                val newPieces = updated.filter { it.type != PieceType.STRIKER }

                _uiState.update {
                    it.copy(
                        pieces = newPieces,
                        striker = newStriker,
                        particles = newParticles
                    )
                }

                // Check if all pieces stopped moving
                val anyMoving = updated.any { !it.isPocketed && it.isMoving }
                if (!anyMoving) {
                    delay(300) // Small settle delay
                    break
                }

                delay(16) // ~60fps
            }

            onTurnFinished()
        }
    }

    private fun onTurnFinished() {
        val state = _uiState.value
        val player = state.currentPlayer
        var nextPlayer = if (player == 1) 2 else 1
        var p1Score = state.player1Score
        var p2Score = state.player2Score
        var p1White = state.player1PocketedWhite
        var p1Black = state.player1PocketedBlack
        var p2White = state.player2PocketedWhite
        var p2Black = state.player2PocketedBlack
        var queenHolder = state.queenPocketedBy
        var isQueenCoverReq = state.isQueenCoverRequired
        var alert: String? = null
        var retainTurn = false

        when (state.gameMode) {
            GameMode.CLASSIC_POINTS, GameMode.PASS_AND_PLAY, GameMode.VS_AI, GameMode.PRACTICE -> {
                // Check striker foul
                if (strikerPocketedThisTurn) {
                    soundManager.playFoul()
                    alert = "FOUL! Striker Pocketed (-5 Pts & Penalty)"
                    if (player == 1) p1Score = maxOf(0, p1Score - 5) else p2Score = maxOf(0, p2Score - 5)
                    // Return a penalty coin to center if available
                    returnPenaltyPieceToCenter(player)
                    retainTurn = false
                } else {
                    // Check pieces pocketed
                    val pocketedPieces = currentShotPocketed.filter { it.type != PieceType.STRIKER }
                    if (pocketedPieces.isNotEmpty()) {
                        retainTurn = true // Carrom rule: potting a piece retains turn
                        for (p in pocketedPieces) {
                            val pts = when (p.type) {
                                PieceType.WHITE -> 20
                                PieceType.BLACK -> 10
                                PieceType.QUEEN -> 50
                                else -> 0
                            }
                            if (player == 1) {
                                p1Score += pts
                                if (p.type == PieceType.WHITE) p1White++
                                if (p.type == PieceType.BLACK) p1Black++
                            } else {
                                p2Score += pts
                                if (p.type == PieceType.WHITE) p2White++
                                if (p.type == PieceType.BLACK) p2Black++
                            }

                            if (p.type == PieceType.QUEEN) {
                                queenHolder = player
                                isQueenCoverReq = true
                                alert = "Queen Pocketed! Must cover with any piece!"
                            }
                        }

                        // Queen cover check: if Queen was previously potted and cover was required, did we pot another piece?
                        if (isQueenCoverReq && !queenPocketedThisTurn) {
                            isQueenCoverReq = false
                            alert = "Queen Covered Successfully! +50 Pts Locked!"
                        }
                    } else {
                        // Missed shot
                        if (isQueenCoverReq) {
                            // Failed queen cover: Queen returns to center!
                            soundManager.playFoul()
                            alert = "Queen Cover Failed! Queen returns to center."
                            returnQueenToCenter()
                            if (queenHolder == 1) p1Score = maxOf(0, p1Score - 50)
                            else if (queenHolder == 2) p2Score = maxOf(0, p2Score - 50)
                            queenHolder = null
                            isQueenCoverReq = false
                        }
                        retainTurn = false
                    }
                }
            }

            GameMode.DISC_POOL -> {
                if (strikerPocketedThisTurn) {
                    soundManager.playFoul()
                    alert = "FOUL! Striker Pocketed"
                    retainTurn = false
                } else {
                    val assignedColor = if (player == 1) state.player1AssignedColor else state.player2AssignedColor
                    val myPotted = currentShotPocketed.filter { it.type == assignedColor || it.type == PieceType.QUEEN }
                    val opponentPotted = currentShotPocketed.filter {
                        val oppColor = if (player == 1) state.player2AssignedColor else state.player1AssignedColor
                        it.type == oppColor
                    }

                    if (myPotted.isNotEmpty()) {
                        retainTurn = true
                        for (p in myPotted) {
                            if (player == 1) {
                                if (p.type == PieceType.WHITE) p1White++ else if (p.type == PieceType.BLACK) p1Black++
                                p1Score += 10
                            } else {
                                if (p.type == PieceType.WHITE) p2White++ else if (p.type == PieceType.BLACK) p2Black++
                                p2Score += 10
                            }
                        }
                    } else {
                        retainTurn = false
                    }

                    // Opponent piece potted by you credits opponent
                    if (opponentPotted.isNotEmpty()) {
                        for (p in opponentPotted) {
                            if (player == 1) {
                                if (p.type == PieceType.WHITE) p2White++ else if (p.type == PieceType.BLACK) p2Black++
                                p2Score += 10
                            } else {
                                if (p.type == PieceType.WHITE) p1White++ else if (p.type == PieceType.BLACK) p1Black++
                                p1Score += 10
                            }
                        }
                    }
                }
            }

            GameMode.TRICK_SHOTS -> {
                val level = state.trickShotLevel
                if (level != null) {
                    val targetPotted = currentShotPocketed.filter { it.type in level.targetPieceTypes }
                    val newCount = state.trickShotTargetCount + targetPotted.size
                    _uiState.update { it.copy(trickShotTargetCount = newCount) }

                    if (newCount >= level.requiredPockets) {
                        // Level Complete!
                        val stars = when {
                            state.trickShotShotsTaken <= 1 -> 3
                            state.trickShotShotsTaken <= level.maxShots -> 2
                            else -> 1
                        }
                        soundManager.playVictory()
                        _uiState.update {
                            it.copy(
                                isGameOver = true,
                                winnerName = "Level Cleared!",
                                trickShotStarsEarned = stars,
                                alertMessage = "Awesome Shot! $stars Stars Earned!"
                            )
                        }
                        viewModelScope.launch {
                            repository.saveTrickShotStars(level.id, stars, level.coinReward)
                        }
                        return
                    } else if (state.trickShotShotsTaken >= level.maxShots) {
                        // Failed level
                        soundManager.playFoul()
                        _uiState.update {
                            it.copy(
                                isGameOver = true,
                                winnerName = "Challenge Failed",
                                alertMessage = "Out of shots! Try again!"
                            )
                        }
                        return
                    }
                    retainTurn = true
                }
            }
        }

        // Check Victory Condition
        val unPocketed = _uiState.value.pieces.filter { !it.isPocketed }
        var gameOver = false
        var winner: String? = null

        if (unPocketed.isEmpty()) {
            gameOver = true
            winner = if (p1Score > p2Score) state.player1Name else state.player2Name
            soundManager.playVictory()
            recordMatchEnd(winner, p1Score, p2Score)
        } else if (state.gameMode == GameMode.DISC_POOL) {
            val p1Assigned = state.player1AssignedColor
            val p2Assigned = state.player2AssignedColor
            val p1Remaining = unPocketed.count { it.type == p1Assigned }
            val p2Remaining = unPocketed.count { it.type == p2Assigned }

            if (p1Remaining == 0) {
                gameOver = true
                winner = state.player1Name
                soundManager.playVictory()
                recordMatchEnd(winner, p1Score, p2Score)
            } else if (p2Remaining == 0) {
                gameOver = true
                winner = state.player2Name
                soundManager.playVictory()
                recordMatchEnd(winner, p1Score, p2Score)
            }
        }

        val activePlayerTurn = if (retainTurn) player else nextPlayer
        val newStriker = createStrikerForPlayer(activePlayerTurn, state.strikerTheme, 0.5f)

        _uiState.update {
            it.copy(
                isPhysicsRunning = false,
                currentPlayer = activePlayerTurn,
                player1Score = p1Score,
                player2Score = p2Score,
                player1PocketedWhite = p1White,
                player1PocketedBlack = p1Black,
                player2PocketedWhite = p2White,
                player2PocketedBlack = p2Black,
                queenPocketedBy = queenHolder,
                isQueenCoverRequired = isQueenCoverReq,
                striker = newStriker,
                strikerSliderFraction = 0.5f,
                strikerAimVector = Vector2D(0f, if (activePlayerTurn == 1) -1f else 1f),
                shotPowerPercent = 50f,
                isGameOver = gameOver,
                winnerName = winner,
                alertMessage = alert ?: if (retainTurn) "Great Shot! ${if (activePlayerTurn == 1) state.player1Name else state.player2Name} takes another shot"
                else "Turn: ${if (activePlayerTurn == 1) state.player1Name else state.player2Name}"
            )
        }

        // Trigger AI Turn if Vs AI and active player is 2
        if (!gameOver && state.gameMode == GameMode.VS_AI && activePlayerTurn == 2) {
            triggerAiBotTurn()
        }
    }

    private fun triggerAiBotTurn() {
        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            _uiState.update { it.copy(isAiThinking = true, alertMessage = "Bot is calculating shot...") }
            delay(900)

            val state = _uiState.value
            val targetTypes = if (state.gameMode == GameMode.DISC_POOL) {
                setOf(state.player2AssignedColor, PieceType.QUEEN)
            } else {
                setOf(PieceType.WHITE, PieceType.BLACK, PieceType.QUEEN)
            }

            val (bestSliderX, aimPowerVec) = CarromPhysics.computeAiShot(
                pieces = state.pieces,
                targetTypes = targetTypes,
                difficulty = state.aiDifficulty,
                isTopPlayer = true
            )

            val fraction = ((bestSliderX - CarromPhysics.BASELINE_MIN_X) /
                    (CarromPhysics.BASELINE_MAX_X - CarromPhysics.BASELINE_MIN_X)).coerceIn(0f, 1f)

            _uiState.update {
                it.copy(
                    strikerSliderFraction = fraction,
                    striker = createStrikerForPlayer(2, it.strikerTheme, fraction)
                )
            }

            delay(600)

            val power = aimPowerVec.length().coerceIn(20f, 100f)
            val aimNorm = aimPowerVec.normalized()

            _uiState.update {
                it.copy(
                    strikerAimVector = aimNorm,
                    shotPowerPercent = power,
                    isAiThinking = false
                )
            }

            delay(300)
            onAimRelease()
        }
    }

    private fun returnQueenToCenter() {
        val pieces = _uiState.value.pieces.toMutableList()
        val queenIdx = pieces.indexOfFirst { it.type == PieceType.QUEEN }
        if (queenIdx >= 0) {
            pieces[queenIdx] = pieces[queenIdx].copy(
                position = Vector2D(500f, 500f),
                velocity = Vector2D(0f, 0f),
                isPocketed = false,
                pocketAnimProgress = 0f
            )
            _uiState.update { it.copy(pieces = pieces) }
        }
    }

    private fun returnPenaltyPieceToCenter(player: Int) {
        val assignedColor = if (player == 1) _uiState.value.player1AssignedColor else _uiState.value.player2AssignedColor
        val pieces = _uiState.value.pieces.toMutableList()
        val pocketedIdx = pieces.indexOfFirst { it.isPocketed && (it.type == assignedColor || it.type == PieceType.BLACK) }
        if (pocketedIdx >= 0) {
            val centerOffset = Vector2D(500f + Random.nextFloat() * 20f - 10f, 500f + Random.nextFloat() * 20f - 10f)
            pieces[pocketedIdx] = pieces[pocketedIdx].copy(
                position = centerOffset,
                velocity = Vector2D(0f, 0f),
                isPocketed = false,
                pocketAnimProgress = 0f
            )
            _uiState.update { it.copy(pieces = pieces) }
        }
    }

    private fun recordMatchEnd(winner: String?, p1Score: Int, p2Score: Int) {
        val state = _uiState.value
        val isWin = winner == state.player1Name
        val coinsEarned = if (isWin) 250 else 50

        viewModelScope.launch {
            repository.recordMatch(
                mode = state.gameMode.displayName,
                p1Name = state.player1Name,
                p2Name = state.player2Name,
                p1Score = p1Score,
                p2Score = p2Score,
                winnerName = winner ?: "Tie",
                totalTurns = 10,
                coinsEarned = coinsEarned,
                isWin = isWin,
                queenPocketed = state.player1PocketedWhite > 0,
                piecesPocketed = state.player1PocketedWhite + state.player1PocketedBlack
            )
        }
    }

    private fun spawnPocketParticles(pos: Vector2D) {
        val colors = listOf(0xFFFFD700, 0xFFFF3D00, 0xFF00E5FF, 0xFFFFFFFF, 0xFF76FF03)
        val newParticles = mutableListOf<Particle>()
        for (i in 0 until 18) {
            val angle = (Random.nextFloat() * 2f * PI).toFloat()
            val speed = 80f + Random.nextFloat() * 160f
            newParticles.add(
                Particle(
                    position = pos,
                    velocity = Vector2D(cos(angle) * speed, sin(angle) * speed),
                    color = colors.random(),
                    radius = 3f + Random.nextFloat() * 4f,
                    alpha = 1.0f,
                    life = 1.0f
                )
            )
        }
        _uiState.update { it.copy(particles = it.particles + newParticles) }
    }

    private fun updateParticles(particles: List<Particle>, dt: Float): List<Particle> {
        val updated = mutableListOf<Particle>()
        for (p in particles) {
            val life = p.life - dt * 2.5f
            if (life > 0f) {
                updated.add(
                    p.copy(
                        position = p.position + p.velocity * dt,
                        velocity = p.velocity * 0.95f,
                        alpha = life,
                        life = life
                    )
                )
            }
        }
        return updated
    }

    fun togglePause() {
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun selectBoardTheme(theme: BoardTheme) {
        _uiState.update { it.copy(boardTheme = theme) }
        viewModelScope.launch { repository.selectBoard(theme.id) }
    }

    fun selectStrikerTheme(theme: StrikerTheme) {
        _uiState.update { it.copy(strikerTheme = theme) }
        viewModelScope.launch { repository.selectStriker(theme.id) }
    }

    fun unlockBoard(theme: BoardTheme, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.unlockBoard(theme.id, theme.price)
            if (success) selectBoardTheme(theme)
            onResult(success)
        }
    }

    fun unlockStriker(theme: StrikerTheme, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.unlockStriker(theme.id, theme.price)
            if (success) selectStrikerTheme(theme)
            onResult(success)
        }
    }

    fun toggleAudioSettings(sound: Boolean, haptics: Boolean) {
        soundManager.soundEnabled = sound
        soundManager.hapticsEnabled = haptics
        viewModelScope.launch {
            repository.toggleAudio(sound, haptics)
        }
    }

    fun claimDailyReward() {
        viewModelScope.launch {
            repository.addCoins(300)
            soundManager.playVictory()
        }
    }
}
