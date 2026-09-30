package com.example.minniproj.model

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {

    // ==================================================
    // ÉTAT DE LA POPUP
    // ==================================================

    var showDialog by remember {
        mutableStateOf(false)
    }


    // ==================================================
    // COULEURS
    // ==================================================

    val primaryColor =
        MaterialTheme.colorScheme.primary

    val backgroundColor =
        MaterialTheme.colorScheme.background

    val surfaceColor =
        MaterialTheme.colorScheme.surface


    // ==================================================
    // ÉCRAN PROFILE
    // ==================================================

    Column(

        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {


        // ==================================================
        // CERCLE DE PROFIL
        // ==================================================

        Surface(

            modifier =
                Modifier.size(110.dp),

            shape =
                CircleShape,

            color =
                primaryColor.copy(alpha = 0.12f)
        ) {

            Column(

                modifier =
                    Modifier.fillMaxSize(),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(

                    text = "👤",

                    fontSize = 45.sp
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(24.dp)
        )


        // ==================================================
        // TITRE
        // ==================================================

        Text(

            text =
                "Bienvenue !",

            fontSize =
                28.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                MaterialTheme.colorScheme.onBackground
        )


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        // ==================================================
        // DESCRIPTION
        // ==================================================

        Text(

            text =
                "Connectez-vous pour accéder à votre profil et retrouver vos livres.",

            fontSize =
                15.sp,

            lineHeight =
                22.sp,

            color =
                MaterialTheme.colorScheme.onBackground
                    .copy(alpha = 0.65f),

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(32.dp)
        )


        // ==================================================
        // CARTE DE CONNEXION
        // ==================================================

        Surface(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(20.dp),

            color =
                surfaceColor,

            tonalElevation =
                4.dp
        ) {

            Column(

                modifier =
                    Modifier.padding(24.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {


                // ==========================================
                // PETIT INDICATEUR
                // ==========================================

                Surface(

                    modifier =
                        Modifier.size(12.dp),

                    shape =
                        CircleShape,

                    color =
                        primaryColor
                ) {}


                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )


                // ==========================================
                // TITRE
                // ==========================================

                Text(

                    text =
                        "Votre espace personnel",

                    fontSize =
                        19.sp,

                    fontWeight =
                        FontWeight.Bold,

                    textAlign =
                        TextAlign.Center
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                // ==========================================
                // DESCRIPTION
                // ==========================================

                Text(

                    text =
                        "Connectez-vous pour personnaliser votre expérience.",

                    fontSize =
                        14.sp,

                    lineHeight =
                        20.sp,

                    textAlign =
                        TextAlign.Center,

                    color =
                        MaterialTheme.colorScheme.onSurface
                            .copy(alpha = 0.60f)
                )


                Spacer(
                    modifier =
                        Modifier.height(22.dp)
                )


                // ==========================================
                // BOUTON
                // ==========================================

                Button(

                    onClick = {
                        showDialog = true
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                primaryColor
                        )
                ) {

                    Text(

                        text =
                            "Se connecter",

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(24.dp)
        )


        // ==================================================
        // TEXTE DU BAS
        // ==================================================

        Text(

            text =
                "La connexion sera bientôt disponible.",

            fontSize =
                12.sp,

            color =
                MaterialTheme.colorScheme.onBackground
                    .copy(alpha = 0.45f),

            textAlign =
                TextAlign.Center
        )
    }


    // ==================================================
    // POPUP
    // ==================================================

    if (showDialog) {

        AlertDialog(

            onDismissRequest = {
                showDialog = false
            },

            shape =
                RoundedCornerShape(24.dp),

            title = {

                Text(

                    text =
                        "Bientôt disponible",

                    fontSize =
                        21.sp,

                    fontWeight =
                        FontWeight.Bold,

                    textAlign =
                        TextAlign.Center
                )
            },

            text = {

                Text(

                    text =
                        "La fonctionnalité de connexion n'est pas encore disponible. Elle sera ajoutée dans une prochaine version.",

                    textAlign =
                        TextAlign.Center,

                    lineHeight =
                        21.sp,

                    color =
                        MaterialTheme.colorScheme.onSurface
                            .copy(alpha = 0.70f)
                )
            },

            confirmButton = {

                Button(

                    onClick = {
                        showDialog = false
                    },

                    shape =
                        RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "Compris"
                    )
                }
            }
        )
    }
}