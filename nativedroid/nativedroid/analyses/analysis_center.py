import logging

__author__ = "Fengguo Wei"
__copyright__ = "Copyright 2018, The Argus-SAF Project"
__license__ = "Apache v2.0"

nativedroid_logger = logging.getLogger('AnnotationBasedAnalysis')


class AnalysisCenter(object):
    """
    This class is used to hold nativedroid analysis related util classes.

    :param JNSafClient jnsaf_client: JNSaf client
    :param SourceAndSinkManager ssm:
    """
    def __init__(self, jnsaf_client, ssm):
        self._jnsaf_client = jnsaf_client
        self._ssm = ssm

    def get_jnsaf_client(self):
        return self._jnsaf_client

    def get_source_sink_manager(self):
        return self._ssm