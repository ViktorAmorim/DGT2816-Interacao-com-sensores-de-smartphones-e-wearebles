/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.doma.presentation

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.doma.R
import com.example.doma.presentation.theme.DomaTheme
import android.provider.Settings
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity


class MainActivity : ComponentActivity() {

    private lateinit var audioHelper: AudioHelper
    private lateinit var speechHelper: SpeechHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioHelper = AudioHelper(this)
        speechHelper = SpeechHelper(this)
        registerAudioCallback()

        setContent{
            WearApp("Doma", audioHelper, speechHelper)
        }
    }

    private fun registerAudioCallback(){
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        audioManager.registerAudioDeviceCallback(
            object : android.media.AudioDeviceCallback(){

                override fun onAudioDevicesAdded(addedDevices: Array<android.media.AudioDeviceInfo>?){
                    val bluetoothConnected = addedDevices?.any{
                        it.type == android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                    } ?: false
                    if(bluetoothConnected){
                        speechHelper.speak("Fone Bluetooth conectado")
                    }
                }

                override fun onAudioDevicesRemoved(removedDevices: Array<android.media.AudioDeviceInfo>?){
                    val bluetoothDisconnected = removedDevices?.any{
                        it.type == android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                    } ?: false
                    if(bluetoothDisconnected){
                        speechHelper.speak("Fone Bluetooth desconectado")
                    }
                }

            }, null
        )

    }




}

@Composable
fun WearApp(greetingName: String, audioHelper: AudioHelper, speechHelper: SpeechHelper) {
    DomaTheme {
        AppScaffold {
            val listState = rememberTransformingLazyColumnState()
            val transformationSpec = rememberTransformationSpec()
            ScreenScaffold(
                scrollState = listState,
                edgeButton = {
                    EdgeButton(
                        onClick = { /*TODO*/ },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                    ) {
                        Text("More")
                    }
                },
            ) { contentPadding -> // ScreenScaffold provides default padding; adjust as needed
                TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
                    item {
                        ListHeader(
                            modifier =
                                Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text(text = stringResource(R.string.hello_world, greetingName))
                        }
                    }

                    // botão de checar se o autofalante esta disponivel
                    item {
                        Button(
                            onClick = {
                                val available = audioHelper.audioOutputAvailable(
                                    android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
                                )
                                speechHelper.speak(
                                    if(available) "Alto falante disponivel"
                                    else "Alto falante não disponivel"
                                )
                            },

                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text("Ver Autofalante")
                        }
                    }

                    // botao para checar se o bluetooth esta conectado
                    item {
                        Button(
                            onClick = {
                                val available = audioHelper.audioOutputAvailable(
                                    android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                                )

                                speechHelper.speak(
                                    if(available)"Fone Bluetooth conectado"
                                    else "Nenhum fone Bluetooth conectado"
                                )
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text("Ver Bluetooth")
                        }
                    }

                    // botao para checagem de notificações
                    item {
                        Button(
                            onClick = {
                                val notification = fakeNotifications.random()
                                speechHelper.speak(notification)
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ){
                            Text("Ler notificações")
                        }
                    }

                    // botão de alerta de segurança
                    item {
                        Button(
                            onClick = {
                                speechHelper.speak(
                                    "Alerta de segurança. Situação critica detectada. Verifique imediatamente."
                                )
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text("Alerta de Segurança")
                        }
                    }
                    item {
                        val context = LocalContext.current
                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply{
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),

                        ){
                            Text("Abrir Bluetooth")
                        }
                    }

                }
            }
        }
    }
}

// mensagens estaticas para simular uma notificação para o botão Ler notificações
val fakeNotifications = listOf(
    "Sistema Doma: reunião com equipe inicia em 10 minutos",
    "Segurança: acesso detectado na área restrita do setor 3",
    "RH: novo comunicado disponível no portal interno",
    "Treinamento: lembrete de atividade obrigatória hoje às 14h",
    "Alerta: atualização crítica do sistema foi aplicada com sucesso"
)

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearAppPreview()
}

@Composable
fun WearAppPreview() {
    DomaTheme {
        AppScaffold {
            Text("Preview Android")
        }
    }
}