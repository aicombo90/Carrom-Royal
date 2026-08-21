package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.CarromPhysics
import com.example.model.PieceType
import com.example.model.Vector2D
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Carrom Royale", appName)
  }

  @Test
  fun `carrom physics piece creation has queen and 18 coins`() {
    val pieces = CarromPhysics.createStandardPieces()
    assertEquals(19, pieces.size)
    val queens = pieces.filter { it.type == PieceType.QUEEN }
    val whites = pieces.filter { it.type == PieceType.WHITE }
    val blacks = pieces.filter { it.type == PieceType.BLACK }
    assertEquals(1, queens.size)
    assertEquals(9, whites.size)
    assertEquals(9, blacks.size)
  }

  @Test
  fun `vector math distance and normal calculation`() {
    val v1 = Vector2D(0f, 0f)
    val v2 = Vector2D(3f, 4f)
    assertEquals(5f, v1.distanceTo(v2), 0.001f)
    val norm = v2.normalized()
    assertEquals(0.6f, norm.x, 0.001f)
    assertEquals(0.8f, norm.y, 0.001f)
  }
}
