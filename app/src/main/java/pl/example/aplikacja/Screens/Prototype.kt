package pl.example.aplikacja.Screens



import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BlurredHexagonBox() {

}

@Composable
fun PrototypeScreen(){

    Box{
        BlurredHexagonBox()
    }


}

@Preview(showBackground = true)
@Composable
fun PrototypeScreenPreview() {
    PrototypeScreen()
}