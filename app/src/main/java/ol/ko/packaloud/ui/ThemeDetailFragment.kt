package ol.ko.packaloud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ol.ko.packaloud.R
import ol.ko.packaloud.SpeechConfigRepository
import ol.ko.packaloud.SpeechConfigStore.speechConfigPrefsDataStore
import ol.ko.packaloud.databinding.FragmentThemeDetailBinding

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ThemeListFragment]
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
class ThemeDetailFragment : Fragment() {

    private lateinit var binding: FragmentThemeDetailBinding
    private val viewModel by activityViewModels<ThemeListViewModel>()
    private val args: ThemeDetailFragmentArgs by navArgs()

    private var theme: PackTheme? = null
    private var rate = "0.85"
    private var style = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        theme = viewModel.getThemeById(args.itemId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThemeDetailBinding.inflate(inflater, container, false)
        updateContent()
        val settingsRepo = SpeechConfigRepository(requireContext().speechConfigPrefsDataStore)
        lifecycleScope.launch {
            settingsRepo.loadRate().first()?.let { rate = it }
            settingsRepo.loadStyle().first()?.let { style = it }
        }
        return binding.root
    }

    private fun updateContent() {
        (activity as AppCompatActivity?)?.supportActionBar?.title = theme?.theme
        theme?.let { packTheme ->
            binding.itemTitle.text = packTheme.theme
            listOf(
                binding.question10 to binding.answer10,
                binding.question20 to binding.answer20,
                binding.question30 to binding.answer30,
                binding.question40 to binding.answer40,
                binding.question50 to binding.answer50
            ).forEachIndexed { index, (questionTv, answerTv) ->
                questionTv.text = "${index + 1}0. ${packTheme.questions[index]}"
                questionTv.setOnClickListener {
                    questionTv.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.dark_brown
                    ))
                    viewModel.speechService.readAloud(packTheme.questions[index], rate, style)
                }
                answerTv.setOnClickListener { answerTv.text = packTheme.answers[index] }
            }
        }
    }
}