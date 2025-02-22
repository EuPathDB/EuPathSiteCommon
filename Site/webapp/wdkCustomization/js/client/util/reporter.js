import { Components } from 'wdk-client';

// load individual reporter forms
import TabularReporterForm from '../components/reporters/TabularReporterForm';
import TextReporterForm from '../components/reporters/TextReporterForm';
import XmlReporterForm from '../components/reporters/XmlReporterForm';
import JsonReporterForm from '../components/reporters/JsonReporterForm';
import Gff3ReporterForm from '../components/reporters/Gff3ReporterForm';
import FastaGeneReporterForm from '../components/reporters/FastaGeneReporterForm';
import FastaGenomicSequenceReporterForm from '../components/reporters/FastaGenomicSequenceReporterForm';
import FastaOrfReporterForm from '../components/reporters/FastaOrfReporterForm';
import TableReporterForm from '../components/reporters/TableReporterForm';
import TranscriptTableReporterForm from '../components/reporters/TranscriptTableReporterForm';
import TranscriptAttributesReporterForm from '../components/reporters/TranscriptAttributesReporterForm';

let EmptyReporter = props => ( <noscript/> );

EmptyReporter.getInitialState = () => ({ formState: null, formUiState: null });

export function selectReporterComponent(reporterName, recordClassName) {
  switch (reporterName) {
    case 'attributesTabular':
      switch (recordClassName) {
        case 'TranscriptRecordClasses.TranscriptRecordClass':
          return TranscriptAttributesReporterForm;
        default:
          return TabularReporterForm;
      }
    case 'tableTabular':
      switch (recordClassName) {
        case 'TranscriptRecordClasses.TranscriptRecordClass':
          return TranscriptTableReporterForm;
        default:
          return TableReporterForm;
      }
    case 'tabular':
      return TabularReporterForm;
    case 'srt':
      switch (recordClassName) {
        // both gene and transcript use the same reporter
        case 'GeneRecordClasses.GeneRecordClass':
        case 'TranscriptRecordClasses.TranscriptRecordClass':
          return FastaGeneReporterForm;
        case 'SequenceRecordClasses.SequenceRecordClass':
          return FastaGenomicSequenceReporterForm;
        case 'OrfRecordClasses.OrfRecordClass':
          return FastaOrfReporterForm;
        default:
          console.error("Unsupported FASTA recordClass: " + recordClassName);
          return EmptyReporter;
      }
    case 'gff3':
      return Gff3ReporterForm;
    case 'fullRecord':
      return TextReporterForm;
    case 'xml':
      return XmlReporterForm;
    case 'json':
      return JsonReporterForm;
    // uncomment if adding service json reporter to model
    //case 'wdk-service-json':
    //  return Components.WdkServiceJsonReporterForm;
    default:
      return EmptyReporter;
  }
}
