<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.ScrollPane?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.layout.FlowPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.VBox?>

<AnchorPane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="800.0" prefWidth="1200.0" style="-fx-background-color: #F3F4F6;" xmlns="http://javafx.com/javafx/23.0.1" xmlns:fx="http://javafx.com/fxml/1" fx:controller="tn.esprit.workshop.gui.ListeCandidaturesController">
    <children>
        <!-- Contenu principal -->
        <VBox layoutX="6.0" prefHeight="800.0" prefWidth="1200.0" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="6.0" AnchorPane.rightAnchor="-6.0" AnchorPane.topAnchor="0.0">
            <children>
                <!-- Titre -->
                <HBox alignment="CENTER" spacing="20.0" style="-fx-padding: 20 30;">
                    <children>
                        <Label text="Listes des Candidatures" style="-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;" />
                    </children>
                </HBox>

                <!-- Grille de candidatures -->
                <ScrollPane fitToWidth="true" style="-fx-background-color: transparent; -fx-padding: 0;" VBox.vgrow="ALWAYS">
                    <content>
                        <FlowPane fx:id="candidaturesContainer" hgap="20" style="-fx-padding: 20;" vgap="20">
                            <!-- Les cartes seront ajoutées dynamiquement ici -->
                        </FlowPane>
                    </content>
                </ScrollPane>

                <!-- Pagination -->
                <HBox alignment="CENTER" spacing="10" style="-fx-padding: 10;">
                    <Button fx:id="btnPrevious" onAction="#previousPage" text="Précédent" style="-fx-background-color: #D4A76A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 20;" />
                    <Label fx:id="pageIndicator" text="Page 1" style="-fx-font-size: 14px; -fx-text-fill: #333;" />
                    <Button fx:id="btnNext" onAction="#nextPage" text="Suivant" style="-fx-background-color: #D4A76A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 20;" />
                </HBox>
            </children>
        </VBox>
    </children>
</AnchorPane>